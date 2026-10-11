package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.dto.CommunityReportDto;
import com.authentica.model.CommunityReport;
import com.authentica.model.User;
import com.authentica.repository.UserRepository;
import com.authentica.service.CommunityReportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityReportController {

    private final CommunityReportService reportService;
    private final UserRepository userRepository;

    public CommunityReportController(CommunityReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<CommunityReport>>> getFeed(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Page<CommunityReport> reports = reportService.getVerifiedReports(page, size);
        return ResponseEntity.ok(ApiResponse.success("Threat reports feed fetched.", reports));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<CommunityReport>>> getRecent() {
        return ResponseEntity.ok(ApiResponse.success("Recent threat reports fetched.", reportService.getRecentReports()));
    }

    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<CommunityReport>> createReport(@RequestBody CommunityReportDto.CreateRequest request,
                                                                     Authentication authentication) {
        Long userId = null;
        if (authentication != null) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) userId = user.getId();
        }

        CommunityReport report = reportService.createReport(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Report submitted to community moderation queue.", report));
    }

    @PostMapping("/reports/{id}/upvote")
    public ResponseEntity<ApiResponse<CommunityReport>> upvote(@PathVariable Long id) {
        try {
            CommunityReport report = reportService.upvote(id);
            return ResponseEntity.ok(ApiResponse.success("Report upvoted.", report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reports/{id}/downvote")
    public ResponseEntity<ApiResponse<CommunityReport>> downvote(@PathVariable Long id) {
        try {
            CommunityReport report = reportService.downvote(id);
            return ResponseEntity.ok(ApiResponse.success("Report downvoted.", report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }
}
