package com.authentica.service.engine;

import com.authentica.model.HeuristicRule;
import com.authentica.model.enums.RiskLevel;
import com.authentica.model.enums.RuleCategory;
import com.authentica.model.enums.WhitelistType;
import com.authentica.repository.FraudURLRepository;
import com.authentica.repository.HeuristicRuleRepository;
import com.authentica.repository.WhitelistRepository;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UrlAnalysisEngine {

    private final FraudURLRepository fraudURLRepository;
    private final WhitelistRepository whitelistRepository;
    private final HeuristicRuleRepository ruleRepository;
    private final com.authentica.service.OutboundProbeService outboundProbeService;

    private static final Pattern IP_URL_PATTERN = Pattern.compile(
            "^(http|https)://(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).*"
    );

    public UrlAnalysisEngine(FraudURLRepository fraudURLRepository,
                             WhitelistRepository whitelistRepository,
                             HeuristicRuleRepository ruleRepository,
                             com.authentica.service.OutboundProbeService outboundProbeService) {
        this.fraudURLRepository = fraudURLRepository;
        this.whitelistRepository = whitelistRepository;
        this.ruleRepository = ruleRepository;
        this.outboundProbeService = outboundProbeService;
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

        // 4. Dynamic Suspicious TLD check from Database
        List<HeuristicRule> tldRules = ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SUSPICIOUS_TLD);
        for (HeuristicRule rule : tldRules) {
            if (host.endsWith(rule.getPattern().toLowerCase())) {
                flags.add(rule.getDescription() != null ? rule.getDescription() : "Domain uses high-abuse top-level domain (" + rule.getPattern() + ")");
                threatScore += rule.getWeight();
                break;
            }
        }

        // 5. Dynamic Phishing keywords from Database
        List<HeuristicRule> keywordRules = ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.URL_KEYWORD);
        int keywordMatches = 0;
        for (HeuristicRule rule : keywordRules) {
            if (cleanedUrl.contains(rule.getPattern().toLowerCase())) {
                keywordMatches++;
                flags.add(rule.getDescription() != null ? rule.getDescription() : "Detected security lure keyword: '" + rule.getPattern() + "'");
                threatScore += rule.getWeight();
                if (keywordMatches >= 4) break;
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

        // 8. Live Sandbox SSL Probe & VirusTotal Threat Intelligence
        try {
            com.authentica.service.OutboundProbeService.ProbeResult probeResult = outboundProbeService.probe(cleanedUrl, host);
            if (probeResult != null) {
                if (probeResult.getFlags() != null && !probeResult.getFlags().isEmpty()) {
                    flags.addAll(probeResult.getFlags());
                }
                threatScore += probeResult.getThreatScorePenalty();
            }
        } catch (Exception e) {
            // Non-blocking fallback
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
