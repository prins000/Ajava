package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.ProtectedContact;
import com.authentica.model.User;
import com.authentica.repository.ProtectedContactRepository;
import com.authentica.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contacts")
public class ProtectedContactController {

    private final ProtectedContactRepository contactRepository;
    private final UserRepository userRepository;

    public ProtectedContactController(ProtectedContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProtectedContact>>> getMyContacts(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        List<ProtectedContact> list = contactRepository.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Protected contacts fetched.", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProtectedContact>> addContact(@RequestBody Map<String, Object> body,
                                                                    Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        String contactName = (String) body.get("contactName");
        String phoneNumber = (String) body.get("phoneNumber");
        String relationship = (String) body.getOrDefault("relationship", "Family");

        if (contactName == null || contactName.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Contact name and phone number are required."));
        }

        ProtectedContact contact = new ProtectedContact(user.getId(), contactName.trim(), phoneNumber.trim(), relationship);
        String email = (String) body.get("email");
        if (email != null && !email.trim().isEmpty()) {
            contact.setEmail(email.trim());
        }
        contact.setIsAlertEnabled(true);
        ProtectedContact saved = contactRepository.save(contact);
        return ResponseEntity.ok(ApiResponse.success("Protected family contact registered.", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> removeContact(@PathVariable Long id, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        ProtectedContact contact = contactRepository.findById(id).orElse(null);
        if (contact == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Contact not found."));
        }
        if (!contact.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied to contact."));
        }
        contactRepository.delete(contact);
        return ResponseEntity.ok(ApiResponse.success("Protected contact removed successfully.", "OK"));
    }

    @PatchMapping("/{id}/toggle-alert")
    public ResponseEntity<ApiResponse<ProtectedContact>> toggleAlert(@PathVariable Long id, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }

        ProtectedContact contact = contactRepository.findById(id).orElse(null);
        if (contact == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Contact not found."));
        }
        if (!contact.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied to contact."));
        }
        contact.setIsAlertEnabled(!Boolean.TRUE.equals(contact.getIsAlertEnabled()));
        ProtectedContact saved = contactRepository.save(contact);
        return ResponseEntity.ok(ApiResponse.success("Alert status toggled.", saved));
    }
}
