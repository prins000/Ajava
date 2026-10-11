package com.authentica;

import com.authentica.model.enums.RiskLevel;
import com.authentica.service.engine.EngineResult;
import com.authentica.service.engine.TextAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextAnalysisEngineTest {

    private TextAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        engine = new TextAnalysisEngine();
    }

    @Test
    void testElectricityDisconnectScam() {
        String msg = "Dear consumer your electricity power will be disconnected tonight at 9:30 PM from power office because your previous month bill was not updated.";
        EngineResult result = engine.analyze(msg, null);

        assertTrue(result.getTrustScore() < 75);
        assertTrue(result.getFlags().stream().anyMatch(f -> f.toLowerCase().contains("urgency")));
    }

    @Test
    void testSensitiveDataSolicitation() {
        String msg = "Click link to update KYC immediately or account blocked within 24 hours. Enter your pin and share OTP.";
        EngineResult result = engine.analyze(msg, null);

        assertEquals(RiskLevel.CRITICAL, result.getRiskLevel());
        assertTrue(result.getFlags().size() >= 2);
    }
}
