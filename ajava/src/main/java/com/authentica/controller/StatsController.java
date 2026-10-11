package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.enums.ReportStatus;
import com.authentica.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final AnalysisRepository analysisRepository;
    private final CommunityReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FraudURLRepository fraudURLRepository;
    private final FraudNumberRepository fraudNumberRepository;

    public StatsController(AnalysisRepository analysisRepository,
                           CommunityReportRepository reportRepository,
                           UserRepository userRepository,
                           FraudURLRepository fraudURLRepository,
                           FraudNumberRepository fraudNumberRepository) {
        this.analysisRepository = analysisRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.fraudURLRepository = fraudURLRepository;
        this.fraudNumberRepository = fraudNumberRepository;
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalScans = analysisRepository.count();
        long verifiedReports = reportRepository.countByStatus(ReportStatus.VERIFIED);
        long knownThreats = fraudURLRepository.count() + fraudNumberRepository.count();
        long totalUsers = userRepository.count();

        stats.put("totalScans", totalScans);
        stats.put("verifiedIncidents", verifiedReports);
        stats.put("knownThreats", knownThreats);
        stats.put("totalUsers", totalUsers);
        stats.put("engineCount", "5+");
        stats.put("accuracyRate", "99.4%");
        stats.put("latencyLabel", "< 50 ms");
        stats.put("securityModel", "Zero-trust");

        return ResponseEntity.ok(ApiResponse.success("Public platform statistics retrieved.", stats));
    }
}
