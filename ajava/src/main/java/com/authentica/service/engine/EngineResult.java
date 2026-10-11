package com.authentica.service.engine;

import com.authentica.model.enums.RiskLevel;

import java.util.List;

public class EngineResult {
    private int trustScore;
    private RiskLevel riskLevel;
    private double confidence;
    private String summary;
    private List<String> flags;
    private List<String> recommendations;

    public EngineResult(int trustScore, RiskLevel riskLevel, double confidence, String summary, List<String> flags, List<String> recommendations) {
        this.trustScore = trustScore;
        this.riskLevel = riskLevel;
        this.confidence = confidence;
        this.summary = summary;
        this.flags = flags;
        this.recommendations = recommendations;
    }

    public int getTrustScore() {
        return trustScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getFlags() {
        return flags;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
