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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TextAnalysisEngineTest {

    private HeuristicRuleRepository ruleRepository;
    private TextAnalysisEngine engine;

    @BeforeEach
    void setUp() {
        ruleRepository = Mockito.mock(HeuristicRuleRepository.class);

        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_URGENCY)).thenReturn(List.of(
                new HeuristicRule(RuleCategory.SMS_URGENCY, "electricity", 40, "High psychological urgency lure detected: electricity disconnect"),
                new HeuristicRule(RuleCategory.SMS_URGENCY, "within 24 hours", 40, "High psychological urgency lure detected: within 24 hours")
        ));

        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_CREDENTIAL_TRAP)).thenReturn(List.of(
                new HeuristicRule(RuleCategory.SMS_CREDENTIAL_TRAP, "otp", 50, "Sensitive data solicitation: OTP request"),
                new HeuristicRule(RuleCategory.SMS_CREDENTIAL_TRAP, "pin", 50, "Sensitive data solicitation: PIN request")
        ));

        when(ruleRepository.findByCategoryAndIsActiveTrue(RuleCategory.SMS_FINANCIAL_LURE)).thenReturn(Collections.emptyList());

        engine = new TextAnalysisEngine(ruleRepository);
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
