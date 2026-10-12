package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.User;
import com.authentica.repository.UserRepository;
import com.authentica.service.EmergencyAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final EmergencyAlertService alertService;
    private final UserRepository userRepository;

    public AlertController(EmergencyAlertService alertService, UserRepository userRepository) {
        this.alertService = alertService;
        this.userRepository = userRepository;
    }

    public static class SosRequest {
        private Long contactId;
        private String notes;

        public Long getContactId() { return contactId; }
        public void setContactId(Long contactId) { this.contactId = contactId; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ThreatBroadcastRequest {
        private String target;
        private String type;
        private int threatScore;
        private List<String> flags;

        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getThreatScore() { return threatScore; }
        public void setThreatScore(int threatScore) { this.threatScore = threatScore; }
        public List<String> getFlags() { return flags; }
        public void setFlags(List<String> flags) { this.flags = flags; }
    }

    @PostMapping("/dispatch-sos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dispatchSos(
            @RequestBody SosRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
        }

        boolean sent = alertService.sendSosAlert(user.getId(), request.getContactId(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Emergency Family SOS advisory dispatched successfully.", Map.of("sent", sent)));
    }

    @PostMapping("/broadcast-threat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> broadcastThreat(
            @RequestBody ThreatBroadcastRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
        }

        int count = alertService.broadcastThreatAlert(user.getId(), request.getTarget(), request.getType(), request.getThreatScore(), request.getFlags());
        return ResponseEntity.ok(ApiResponse.success("Alert broadcasted to " + count + " active family shield contacts.", Map.of("dispatchedCount", count)));
    }
}
