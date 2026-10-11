package com.authentica.service.engine;

import com.authentica.model.enums.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TextAnalysisEngine {

    private static final List<String> URGENCY_TRIGGERS = List.of(
            "immediate action", "within 24 hours", "account blocked", "card suspended",
            "electricity will be disconnected", "tonight 9:30 pm", "urgent notice", "last warning",
            "deactivated immediately", "legal action", "arrest warrant", "final notice"
    );

    private static final List<String> FINANCIAL_LURES = List.of(
            "won lottery", "cashback of rs", "congratulations you won", "reward points expired",
            "pre-approved loan", "claim bonus", "free gift", "transfer rs", "credited to your account"
    );

    private static final List<String> CREDENTIAL_TRAPS = List.of(
            "click link to update kyc", "pan card update", "share otp", "enter your pin",
            "download apk", "install anydesk", "install teamviewer", "verify aadhar",
            "update bank details", "send debit card details"
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[\\w\\.-]+(?:\\.[a-zA-Z]{2,})(?:/\\S*)?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+91|0)?[6-9]\\d{9}"
    );

    public EngineResult analyze(String text, String senderNumber) {
        List<String> flags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int threatScore = 0;

        if (text == null || text.isBlank()) {
            return new EngineResult(50, RiskLevel.MEDIUM, 0.5, "Empty text provided for analysis.", flags, recommendations);
        }

        String lowerText = text.toLowerCase();

        // 1. Urgency detection
        for (String trigger : URGENCY_TRIGGERS) {
            if (lowerText.contains(trigger)) {
                flags.add("High psychological urgency lure detected: '" + trigger + "'");
                threatScore += 25;
                break;
            }
        }

        // 2. Financial lure detection
        for (String lure : FINANCIAL_LURES) {
            if (lowerText.contains(lure)) {
                flags.add("Unrealistic financial incentive / reward trigger: '" + lure + "'");
                threatScore += 25;
                break;
            }
        }

        // 3. Credential and sensitive data harvest traps
        for (String trap : CREDENTIAL_TRAPS) {
            if (lowerText.contains(trap)) {
                flags.add("Sensitive data solicitation / remote-access indicator: '" + trap + "'");
                threatScore += 35;
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
