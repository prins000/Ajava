package com.authentica.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_urls")
public class FraudURL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String url;

    @Column(nullable = false)
    private String domain;

    private Integer riskScore = 95;

    private String category = "Phishing";

    @Column(length = 1000)
    private String phishingIndicators;

    private Integer reportCount = 1;

    private LocalDateTime createdAt = LocalDateTime.now();

    public FraudURL() {
    }

    public FraudURL(String url, String domain, Integer riskScore, String category, String phishingIndicators) {
        this.url = url;
        this.domain = domain;
        this.riskScore = riskScore;
        this.category = category;
        this.phishingIndicators = phishingIndicators;
        this.reportCount = 1;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhishingIndicators() {
        return phishingIndicators;
    }

    public void setPhishingIndicators(String phishingIndicators) {
        this.phishingIndicators = phishingIndicators;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
