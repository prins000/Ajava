package com.authentica;

import com.authentica.model.HeuristicRule;
import com.authentica.model.enums.RiskLevel;
import com.authentica.model.enums.RuleCategory;
import com.authentica.repository.HeuristicRuleRepository;
import com.authentica.service.engine.EngineResult;
import com.authentica.service.engine.TextAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TextAnalysisEngineTest {

    private HeuristicRuleRepository ruleRepository;
    private TextAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        ruleRepository = Mockito.mock(HeuristicRuleRepository.class);
        engine = new TextAnalysisEngine(ruleRepository);

        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_URGENCY)).thenReturn(List.of(
                new HeuristicRule(RuleCategory.SMS_URGENCY, "disconnected", 30, "Psychological urgency pressure tactic: 'disconnected'"),
                new HeuristicRule(RuleCategory.SMS_URGENCY, "account blocked", 45, "Psychological urgency pressure tactic: 'account blocked'")
        ));
        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_FINANCIAL_LURE)).thenReturn(List.of());
        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_CREDENTIAL_TRAP)).thenReturn(List.of(
                new HeuristicRule(RuleCategory.SMS_CREDENTIAL_TRAP, "share otp", 45, "Malicious remote access or credential harvest trap: 'share otp'"),
                new HeuristicRule(RuleCategory.SMS_CREDENTIAL_TRAP, "update kyc", 45, "Malicious remote access or credential harvest trap: 'update kyc'")
        ));
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
