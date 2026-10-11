package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.model.AppSettings;
import com.authentica.model.CommunityReport;
import com.authentica.model.User;
import com.authentica.model.enums.ReportStatus;
import com.authentica.service.AdminService;
import com.authentica.service.CommunityReportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final CommunityReportService communityReportService;

    public AdminController(AdminService adminService, CommunityReportService communityReportService) {
        this.adminService = adminService;
        this.communityReportService = communityReportService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Analytics retrieved.", adminService.getAnalyticsOverview()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("User list retrieved.", adminService.getUsers(page, size)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<CommunityReport>>> getAllReports(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Moderation reports retrieved.", communityReportService.getAllReports(page, size)));
    }

    @PatchMapping("/reports/{id}/status")
    public ResponseEntity<ApiResponse<CommunityReport>> updateReportStatus(@PathVariable Long id,
                                                                           @RequestParam ReportStatus status) {
        try {
            CommunityReport report = communityReportService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Report status updated.", report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<AppSettings>> updateSetting(@RequestParam String key,
                                                                  @RequestParam String value,
                                                                  @RequestParam(required = false) String description,
                                                                  @RequestParam(required = false) String category) {
        AppSettings setting = adminService.updateSetting(key, value, description, category);
        return ResponseEntity.ok(ApiResponse.success("System setting updated.", setting));
    }
}
