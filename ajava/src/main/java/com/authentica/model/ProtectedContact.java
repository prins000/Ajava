package com.authentica.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "protected_contacts")
public class ProtectedContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false)
    private String phoneNumber;

    private String relationship;

    private Boolean isAlertEnabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ProtectedContact() {
    }

    public ProtectedContact(Long userId, String contactName, String phoneNumber, String relationship) {
        this.userId = userId;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
        this.isAlertEnabled = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getIsAlertEnabled() {
        return isAlertEnabled;
    }

    public void setIsAlertEnabled(Boolean isAlertEnabled) {
        this.isAlertEnabled = isAlertEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
