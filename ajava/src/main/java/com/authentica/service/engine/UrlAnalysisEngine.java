package com.authentica.service.engine;

import com.authentica.model.enums.RiskLevel;
import com.authentica.repository.FraudURLRepository;
import com.authentica.repository.WhitelistRepository;
import com.authentica.model.enums.WhitelistType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UrlAnalysisEngine {

    private final FraudURLRepository fraudURLRepository;
    private final WhitelistRepository whitelistRepository;

    private static final List<String> SUSPICIOUS_TLDS = Arrays.asList(
            ".tk", ".ml", ".ga", ".cf", ".gq", ".xyz", ".top", ".buzz", ".work", ".click", ".fit", ".casa"
    );

    private static final List<String> PHISHING_KEYWORDS = Arrays.asList(
            "kyc", "sbi", "hdfc", "icici", "pnb", "paytm", "phonepe", "bank", "secure", "login",
            "verify", "blocked", "update-pan", "refund", "lottery", "gift", "electricity-bill", "bonus"
    );

    private static final Pattern IP_URL_PATTERN = Pattern.compile(
            "^(http|https)://(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).*"
    );

    public UrlAnalysisEngine(FraudURLRepository fraudURLRepository, WhitelistRepository whitelistRepository) {
        this.fraudURLRepository = fraudURLRepository;
        this.whitelistRepository = whitelistRepository;
    }

    public EngineResult analyze(String url) {
        List<String> flags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int threatScore = 0; // 0 = Safe, 100 = Highly malicious

        String cleanedUrl = url.trim().toLowerCase();

        // 1. Whitelist Check
        if (whitelistRepository.existsByTypeAndItemValue(WhitelistType.URL, cleanedUrl)) {
            return new EngineResult(95, RiskLevel.SAFE, 0.98, "Verified safe URL in government / banking whitelist.", flags, recommendations);
        }

        // 2. Blacklist / Known Fraud DB Check
        if (fraudURLRepository.existsByUrl(cleanedUrl)) {
            flags.add("Exact URL matches verified cybersecurity fraud intelligence database");
            threatScore += 90;
        }

        // Parse Domain
        String host = "";
        try {
            URI uri = new URI(cleanedUrl.startsWith("http") ? cleanedUrl : "http://" + cleanedUrl);
            host = uri.getHost() != null ? uri.getHost().toLowerCase() : cleanedUrl;
        } catch (Exception e) {
            host = cleanedUrl;
        }

        if (whitelistRepository.existsByTypeAndItemValue(WhitelistType.DOMAIN, host)) {
            return new EngineResult(95, RiskLevel.SAFE, 0.95, "Verified safe domain in official whitelist.", flags, recommendations);
        }

        if (fraudURLRepository.existsByDomain(host)) {
            flags.add("Host domain '" + host + "' is blacklisted for phishing campaigns");
            threatScore += 80;
        }

        // 3. IP address host check
        if (IP_URL_PATTERN.matcher(cleanedUrl).matches() || host.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
            flags.add("URL uses direct IP address instead of legitimate domain name");
            threatScore += 35;
        }

        // 4. Suspicious TLD check
        for (String tld : SUSPICIOUS_TLDS) {
            if (host.endsWith(tld)) {
                flags.add("Domain uses high-abuse top-level domain (" + tld + ")");
                threatScore += 25;
                break;
            }
        }

        // 5. Phishing keywords in domain / path
        int keywordMatches = 0;
        for (String keyword : PHISHING_KEYWORDS) {
            if (cleanedUrl.contains(keyword)) {
                keywordMatches++;
                flags.add("Detected security lure keyword: '" + keyword + "'");
                threatScore += 15;
                if (keywordMatches >= 3) break;
            }
        }

        // 6. Excessive hyphenation / typosquatting tricks
        long hyphenCount = host.chars().filter(ch -> ch == '-').count();
        if (hyphenCount >= 3) {
            flags.add("Domain exhibits deceptive multi-hyphen structure commonly seen in brand impersonation");
            threatScore += 20;
        }

        // 7. HTTPS inspection
        if (!cleanedUrl.startsWith("https://")) {
            flags.add("Connection is unencrypted (HTTP), lacking standard SSL/TLS certification");
            threatScore += 10;
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
            recommendations.add("Do not enter banking credentials, passwords, or personal identity documents.");
            recommendations.add("Do not download files or install APKs prompted by this page.");
            recommendations.add("Block this sender and report to national cybercrime reporting portal (cybercrime.gov.in).");
        } else if (riskLevel == RiskLevel.MEDIUM) {
            recommendations.add("Proceed with caution. Verify the official website domain via search engines.");
            recommendations.add("Check for browser padlock security warnings before submitting forms.");
        } else {
            recommendations.add("Link appears normal. Always verify caller identity before making transactions.");
        }

        String summary = "URL analyzed against threat intelligence rules and domain reputation heuristics. Threat score: " + threatScore + "/100.";

        return new EngineResult(trustScore, riskLevel, 0.92, summary, flags, recommendations);
    }
}
