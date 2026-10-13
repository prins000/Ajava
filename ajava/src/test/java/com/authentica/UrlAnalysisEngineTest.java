package com.authentica;

import com.authentica.model.HeuristicRule;
import com.authentica.model.enums.RiskLevel;
import com.authentica.model.enums.RuleCategory;
import com.authentica.repository.FraudURLRepository;
import com.authentica.repository.HeuristicRuleRepository;
import com.authentica.repository.WhitelistRepository;
import com.authentica.service.OutboundProbeService;
import com.authentica.service.engine.EngineResult;
import com.authentica.service.engine.UrlAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UrlAnalysisEngineTest {

    private FraudURLRepository fraudURLRepository;
    private WhitelistRepository whitelistRepository;
    private HeuristicRuleRepository ruleRepository;
    private OutboundProbeService outboundProbeService;
    private UrlAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        fraudURLRepository = Mockito.mock(FraudURLRepository.class);
        whitelistRepository = Mockito.mock(WhitelistRepository.class);
        ruleRepository = Mockito.mock(HeuristicRuleRepository.class);
        outboundProbeService = Mockito.mock(OutboundProbeService.class);
        engine = new UrlAnalysisEngine(fraudURLRepository, whitelistRepository, ruleRepository, outboundProbeService);

        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.URL_KEYWORD)).thenReturn(List.of(
                new HeuristicRule(RuleCategory.URL_KEYWORD, "kyc", 15, "Security lure keyword detected: 'kyc'"),
                new HeuristicRule(RuleCategory.URL_KEYWORD, "sbi", 15, "Security lure keyword detected: 'sbi'"),
                new HeuristicRule(RuleCategory.URL_KEYWORD, "secure", 15, "Security lure keyword detected: 'secure'"),
                new HeuristicRule(RuleCategory.URL_KEYWORD, "login", 15, "Security lure keyword detected: 'login'")
        ));
        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SUSPICIOUS_TLD)).thenReturn(List.of());
    }

    @Test
    void testPhishingKeywordsDetection() {
        when(fraudURLRepository.existsByUrl(anyString())).thenReturn(false);
        when(fraudURLRepository.existsByDomain(anyString())).thenReturn(false);

        EngineResult result = engine.analyze("http://secure-sbi-update-kyc.com/login");
        assertTrue(result.getTrustScore() < 60);
        assertTrue(result.getFlags().size() > 0);
    }

    @Test
    void testWhitelistedDomain() {
        when(whitelistRepository.existsByTypeAndItemValue(Mockito.any(), Mockito.eq("cybercrime.gov.in"))).thenReturn(true);

        EngineResult result = engine.analyze("https://cybercrime.gov.in");
        assertEquals(RiskLevel.SAFE, result.getRiskLevel());
        assertTrue(result.getTrustScore() >= 90);
    }
}
