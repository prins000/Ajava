package com.authentica.service.engine;

import com.authentica.model.FraudNumber;
import com.authentica.model.enums.RiskLevel;
import com.authentica.model.enums.WhitelistType;
import com.authentica.repository.FraudNumberRepository;
import com.authentica.repository.WhitelistRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class NumberLookupEngine {

    private final FraudNumberRepository fraudNumberRepository;
    private final WhitelistRepository whitelistRepository;

    public NumberLookupEngine(FraudNumberRepository fraudNumberRepository, WhitelistRepository whitelistRepository) {
        this.fraudNumberRepository = fraudNumberRepository;
        this.whitelistRepository = whitelistRepository;
    }

    public EngineResult analyze(String phoneNumber) {
        List<String> flags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int threatScore = 0;

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return new EngineResult(50, RiskLevel.MEDIUM, 0.5, "Invalid phone number provided.", flags, recommendations);
        }

        String cleanedNumber = phoneNumber.replaceAll("[^0-9+]", "").trim();

        // Check Whitelist
        if (whitelistRepository.existsByTypeAndItemValue(WhitelistType.NUMBER, cleanedNumber)) {
            return new EngineResult(95, RiskLevel.SAFE, 0.98, "Verified official institutional number in whitelist.", flags, recommendations);
        }

        // Check Blacklist
        Optional<FraudNumber> fraudOpt = fraudNumberRepository.findByPhoneNumber(cleanedNumber);
        if (fraudOpt.isPresent()) {
            FraudNumber fn = fraudOpt.get();
            flags.add("MATCHED BLACKLIST: Registered spam category: " + fn.getCategory());
            flags.add("Reported by " + fn.getReportCount() + " community members");
            if (fn.getDescription() != null) {
                flags.add("Threat summary: " + fn.getDescription());
            }
            threatScore = fn.getRiskScore() != null ? fn.getRiskScore() : 85;
        } else {
            // General heuristics: International spoofing, virtual number prefixes
            if (cleanedNumber.startsWith("+") && !cleanedNumber.startsWith("+91")) {
                flags.add("International origin calling number (" + cleanedNumber.substring(0, Math.min(4, cleanedNumber.length())) + ") targeting domestic user");
                threatScore += 35;
            } else if (cleanedNumber.length() == 10) {
                flags.add("Standard 10-digit mobile number. No previous scam flags reported.");
                threatScore += 10;
            } else {
                flags.add("Non-standard telephone length or unassigned prefix.");
                threatScore += 20;
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
            recommendations.add("Do not return missed calls from this number.");
            recommendations.add("Block this caller in your device phone dialer.");
            recommendations.add("Submit a community scam report to protect other users.");
        } else {
            recommendations.add("Exercise normal vigilance. Do not share banking OTPs with any caller.");
        }

        String summary = "Phone reputation lookup complete across national blacklist registries and community reports. Threat score: " + threatScore + "/100.";

        return new EngineResult(trustScore, riskLevel, 0.92, summary, flags, recommendations);
    }
}
