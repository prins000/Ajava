package com.authentica.service.engine;

import com.authentica.model.HeuristicRule;
import com.authentica.model.enums.RiskLevel;
import com.authentica.model.enums.RuleCategory;
import com.authentica.repository.HeuristicRuleRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TextAnalysisEngine {

    private final HeuristicRuleRepository ruleRepository;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[\\w\\.-]+(?:\\.[a-zA-Z]{2,})(?:/\\S*)?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+91|0)?[6-9]\\d{9}"
    );

    public TextAnalysisEngine(HeuristicRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public EngineResult analyze(String text, String senderNumber) {
        List<String> flags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int threatScore = 0;

        if (text == null || text.isBlank()) {
            return new EngineResult(50, RiskLevel.MEDIUM, 0.5, "Empty text provided for analysis.", flags, recommendations);
        }

        String lowerText = text.toLowerCase();

        // 1. Dynamic Urgency detection from Database
        List<HeuristicRule> urgencyRules = ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_URGENCY);
        for (HeuristicRule rule : urgencyRules) {
            if (lowerText.contains(rule.getPattern().toLowerCase())) {
                flags.add(rule.getDescription() != null ? rule.getDescription() : "High psychological urgency lure detected: '" + rule.getPattern() + "'");
                threatScore += rule.getWeight();
                break;
            }
        }

        // 2. Dynamic Financial lure detection from Database
        List<HeuristicRule> lureRules = ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_FINANCIAL_LURE);
        for (HeuristicRule rule : lureRules) {
            if (lowerText.contains(rule.getPattern().toLowerCase())) {
                flags.add(rule.getDescription() != null ? rule.getDescription() : "Unrealistic financial incentive / reward trigger: '" + rule.getPattern() + "'");
                threatScore += rule.getWeight();
                break;
            }
        }

        // 3. Dynamic Credential & sensitive trap detection from Database
        List<HeuristicRule> trapRules = ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_CREDENTIAL_TRAP);
        for (HeuristicRule rule : trapRules) {
            if (lowerText.contains(rule.getPattern().toLowerCase())) {
                flags.add(rule.getDescription() != null ? rule.getDescription() : "Sensitive data solicitation / remote-access indicator: '" + rule.getPattern() + "'");
                threatScore += rule.getWeight();
                break;
            }
        }

        // 4. Embedded URL in SMS text
        if (URL_PATTERN.matcher(text).find()) {
            flags.add("Message contains external web link (common in Smishing delivery vectors)");
            threatScore += 15;
        }

        // 5. Fake sender ID impersonation
        if (senderNumber != null && !senderNumber.isBlank()) {
            String cleanSender = senderNumber.trim().toUpperCase();
            if (cleanSender.contains("SBI") || cleanSender.contains("HDFC") || cleanSender.contains("ICICI")) {
                if (cleanSender.length() != 8 && !cleanSender.matches("^[A-Z]{2}-[A-Z0-9]{6}$")) {
                    flags.add("Sender header violates TRAI DLT alphanumeric short-code standard (" + cleanSender + ")");
                    threatScore += 30;
                }
            }
        }

        threatScore = Math.min(100, threatScore);
        int trustScore = Math.max(0, 100 - threatScore);

        RiskLevel riskLevel;
        if (trustScore >= 80) riskLevel = RiskLevel.SAFE;
        else if (trustScore >= 60) riskLevel = RiskLevel.LOW;
        else if (trustScore >= 40) riskLevel = RiskLevel.MEDIUM;
        else if (trustScore >= 20) riskLevel = RiskLevel.HIGH;
        else riskLevel = RiskLevel.CRITICAL;

        if (riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL) {
            recommendations.add("NEVER share OTP, CVV, or UPI PIN with anyone over SMS or phone call.");
            recommendations.add("Banks and electricity boards NEVER ask customers to install third-party APKs.");
            recommendations.add("Forward the fraudulent message to 1909 (Do Not Disturb registry).");
        } else if (riskLevel == RiskLevel.MEDIUM) {
            recommendations.add("Cross-check with your official service provider directly via their mobile app.");
            recommendations.add("Avoid clicking any links sent from unverified mobile numbers.");
        } else {
            recommendations.add("Message shows no typical phishing signatures. Remain alert.");
        }

        String summary = "SMS/Text evaluated against smishing linguistic heuristics, psychological urgency keywords, and phishing intent. Threat score: " + threatScore + "/100.";

        return new EngineResult(trustScore, riskLevel, 0.90, summary, flags, recommendations);
    }
}
