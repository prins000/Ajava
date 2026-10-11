package com.authentica.model;

import com.authentica.model.enums.WhitelistType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "whitelists")
public class Whitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WhitelistType type;

    @Column(nullable = false)
    private String itemValue;

    private String reason;

    private String addedBy = "SYSTEM";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Whitelist() {
    }

    public Whitelist(WhitelistType type, String itemValue, String reason, String addedBy) {
        this.type = type;
        this.itemValue = itemValue;
        this.reason = reason;
        this.addedBy = addedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WhitelistType getType() {
        return type;
    }

    public void setType(WhitelistType type) {
        this.type = type;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
