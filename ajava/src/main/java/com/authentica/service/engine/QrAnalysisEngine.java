package com.authentica.service.engine;

import com.authentica.model.enums.RiskLevel;
import com.authentica.repository.FraudQRRepository;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QrAnalysisEngine {

    private final FraudQRRepository fraudQRRepository;
    private final UrlAnalysisEngine urlAnalysisEngine;
    private final com.authentica.repository.HeuristicRuleRepository ruleRepository;

    public QrAnalysisEngine(FraudQRRepository fraudQRRepository,
                            UrlAnalysisEngine urlAnalysisEngine,
                            com.authentica.repository.HeuristicRuleRepository ruleRepository) {
        this.fraudQRRepository = fraudQRRepository;
        this.urlAnalysisEngine = urlAnalysisEngine;
        this.ruleRepository = ruleRepository;
    }

    public EngineResult analyze(String qrContent) {
        List<String> flags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int threatScore = 0;

        if (qrContent == null || qrContent.isBlank()) {
            return new EngineResult(50, RiskLevel.MEDIUM, 0.5, "Empty QR payload supplied.", flags, recommendations);
        }

        String content = qrContent.trim();

        // 1. Known Fraud QR check
        if (fraudQRRepository.findByQrContent(content).isPresent()) {
            flags.add("QR content matches a verified malicious code in the threat database");
            threatScore += 90;
        }

        // 2. UPI Payment Scheme Analysis (`upi://pay?...`)
        if (content.toLowerCase().startsWith("upi://pay")) {
            Map<String, String> upiParams = parseUpiUri(content);
            String payeeVpa = upiParams.get("pa"); // Payee Address
            String payeeName = upiParams.get("pn"); // Payee Name
            String transactionNote = upiParams.get("tn"); // Transaction Note
            String amount = upiParams.get("am");

            flags.add("Parsed UPI Payment Payload: Payee = " + (payeeName != null ? payeeName : "N/A") + ", VPA = " + (payeeVpa != null ? payeeVpa : "N/A") + (amount != null ? ", Amount = Rs " + amount : ""));

            if (payeeVpa != null) {
                if (fraudQRRepository.findByUpiHandle(payeeVpa).isPresent()) {
                    flags.add("VPA handle '" + payeeVpa + "' is flagged as high-risk in UPI fraud intelligence");
                    threatScore += 85;
                }

                // Dynamic check for deceptive handle names (e.g. refund, customer care, support, electricity)
                String lowerVpa = payeeVpa.toLowerCase();
                List<com.authentica.model.HeuristicRule> qrRules = ruleRepository.findByCategoryAndIsActiveTrue(com.authentica.model.enums.RuleCategory.QR_DECEPTIVE);
                for (com.authentica.model.HeuristicRule rule : qrRules) {
                    if (lowerVpa.contains(rule.getPattern().toLowerCase())) {
                        flags.add(rule.getDescription() != null ? rule.getDescription() : "VPA handle incorporates deceptive impersonation keyword: '" + rule.getPattern() + "'");
                        threatScore += rule.getWeight();
                        break;
                    }
                }
            }

            if (transactionNote != null) {
                String lowerNote = transactionNote.toLowerCase();
                if (lowerNote.contains("receive money") || lowerNote.contains("receive cashback") || lowerNote.contains("claim reward")) {
                    flags.add("CRITICAL: Note misleads user into believing scanning will RECEIVE money rather than PAY money");
                    threatScore += 50;
                }
            }

            // In UPI, scanning a QR code is ONLY to SEND money, never to RECEIVE money!
            recommendations.add("CRITICAL RULE: Scanning a QR code ALWAYS DEBITS your account. You NEVER need to enter your UPI PIN to receive money.");
            recommendations.add("Verify that the merchant name on your UPI screen matches the recipient before authorizing with PIN.");
        } else if (content.startsWith("http://") || content.startsWith("https://")) {
            // Embedded web URL QR code
            flags.add("QR contains embedded external web hyperlink");
            EngineResult urlResult = urlAnalysisEngine.analyze(content);
            threatScore += (100 - urlResult.getTrustScore());
            flags.addAll(urlResult.getFlags());
            recommendations.addAll(urlResult.getRecommendations());
        } else {
            // Plain text or unexpected payload
            flags.add("QR contains arbitrary non-standard payload string");
            threatScore += 15;
            recommendations.add("Inspect payload carefully before taking any action.");
        }

        threatScore = Math.min(100, threatScore);
        int trustScore = Math.max(0, 100 - threatScore);

        RiskLevel riskLevel;
        if (trustScore >= 80) riskLevel = RiskLevel.SAFE;
        else if (trustScore >= 60) riskLevel = RiskLevel.LOW;
        else if (trustScore >= 40) riskLevel = RiskLevel.MEDIUM;
        else if (trustScore >= 20) riskLevel = RiskLevel.HIGH;
        else riskLevel = RiskLevel.CRITICAL;

        String summary = "QR code payload parsed and validated against UPI transaction rules and phishing vectors. Threat score: " + threatScore + "/100.";

        return new EngineResult(trustScore, riskLevel, 0.94, summary, flags, recommendations);
    }

    private Map<String, String> parseUpiUri(String uriStr) {
        Map<String, String> params = new HashMap<>();
        try {
            int queryStart = uriStr.indexOf('?');
            if (queryStart != -1 && queryStart < uriStr.length() - 1) {
                String query = uriStr.substring(queryStart + 1);
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int eq = pair.indexOf('=');
                    if (eq > 0) {
                        String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                        params.put(key, value);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return params;
    }
}
