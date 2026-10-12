package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.Feedback;
import com.authentica.model.User;
import com.authentica.repository.FeedbackRepository;
import com.authentica.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackController(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(@RequestBody Map<String, Object> body,
                                                                 Authentication authentication) {
        Long userId = null;
        String userEmail = (String) body.getOrDefault("userEmail", "anonymous@authentica.io");

        if (authentication != null) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                userId = user.getId();
                userEmail = user.getEmail();
            }
        }

        String category = (String) body.getOrDefault("category", "general");
        Integer rating = body.get("rating") != null ? Integer.parseInt(body.get("rating").toString()) : 5;
        String comment = (String) body.getOrDefault("comment", "");

        if (comment == null || comment.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Comment cannot be empty."));
        }

        Feedback fb = new Feedback(userId, userEmail, category, rating, comment.trim());
        Feedback saved = feedbackRepository.save(fb);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully. Thank you!", saved));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Feedback>>> getAllFeedback() {
        return ResponseEntity.ok(ApiResponse.success("All platform feedback retrieved.", feedbackRepository.findAllByOrderByCreatedAtDesc()));
    }
}
