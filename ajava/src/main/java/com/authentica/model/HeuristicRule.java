package com.authentica.model;

import com.authentica.model.enums.RuleCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "heuristic_rules")
public class HeuristicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RuleCategory category;

    @Column(nullable = false, length = 255)
    private String pattern;

    @Column(nullable = false)
    private Integer weight = 15;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public HeuristicRule() {}

    public HeuristicRule(RuleCategory category, String pattern, Integer weight, String description) {
        this.category = category;
        this.pattern = pattern;
        this.weight = weight != null ? weight : 15;
        this.description = description;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RuleCategory getCategory() { return category; }
    public void setCategory(RuleCategory category) { this.category = category; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
