package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.SupportTicket;
import com.authentica.model.User;
import com.authentica.model.enums.TicketPriority;
import com.authentica.model.enums.TicketStatus;
import com.authentica.repository.SupportTicketRepository;
import com.authentica.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets")
public class SupportTicketController {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;

    public SupportTicketController(SupportTicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupportTicket>> createTicket(@RequestBody Map<String, Object> body,
                                                                   Authentication auth) {
        User user = getAuthenticatedUser(auth);
        Long userId = user != null ? user.getId() : null;
        String userEmail = user != null ? user.getEmail() : (String) body.getOrDefault("userEmail", "anonymous@authentica.io");

        String subject = (String) body.get("subject");
        String description = (String) body.get("description");
        String prioStr = (String) body.getOrDefault("priority", "MEDIUM");

        if (subject == null || subject.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Subject and description are required."));
        }

        TicketPriority priority = TicketPriority.MEDIUM;
        try {
            if (prioStr != null) priority = TicketPriority.valueOf(prioStr.toUpperCase());
        } catch (Exception ignored) {}

        SupportTicket ticket = new SupportTicket(userId, userEmail, subject.trim(), description.trim(), priority);
        SupportTicket saved = ticketRepository.save(ticket);
        return ResponseEntity.ok(ApiResponse.success("Cyber Incident support ticket lodged successfully.", saved));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getMyTickets(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        List<SupportTicket> list = ticketRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Your support tickets retrieved.", list));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<SupportTicket>>> getAllTicketsForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size) {
        Page<SupportTicket> tickets = ticketRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("All incident tickets retrieved.", tickets));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicket>> updateTicketStatus(@PathVariable Long id,
                                                                         @RequestParam TicketStatus status) {
        return ticketRepository.findById(id).map(t -> {
            t.setStatus(status);
            SupportTicket updated = ticketRepository.save(t);
            return ResponseEntity.ok(ApiResponse.success("Ticket status updated to " + status + ".", updated));
        }).orElse(ResponseEntity.status(404).body(ApiResponse.error("Ticket not found.")));
    }
}
