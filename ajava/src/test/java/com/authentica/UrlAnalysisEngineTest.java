package com.authentica;

import com.authentica.model.enums.RiskLevel;
import com.authentica.repository.FraudURLRepository;
import com.authentica.repository.WhitelistRepository;
import com.authentica.service.engine.EngineResult;
import com.authentica.service.engine.UrlAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UrlAnalysisEngineTest {

    private FraudURLRepository fraudURLRepository;
    private WhitelistRepository whitelistRepository;
    private UrlAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        fraudURLRepository = Mockito.mock(FraudURLRepository.class);
        whitelistRepository = Mockito.mock(WhitelistRepository.class);
        engine = new UrlAnalysisEngine(fraudURLRepository, whitelistRepository);
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
