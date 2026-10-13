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
    private final com.authentica.repository.HeuristicRuleRepository ruleRepository;

    public AdminController(AdminService adminService,
                           CommunityReportService communityReportService,
                           com.authentica.repository.HeuristicRuleRepository ruleRepository) {
        this.adminService = adminService;
        this.communityReportService = communityReportService;
        this.ruleRepository = ruleRepository;
    }

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<java.util.List<com.authentica.model.HeuristicRule>>> getRules(
            @RequestParam(required = false) com.authentica.model.enums.RuleCategory category) {
        if (category != null) {
            return ResponseEntity.ok(ApiResponse.success("Filtered threat rules retrieved.", ruleRepository.findByCategoryOrderByCreatedAtDesc(category)));
        }
        return ResponseEntity.ok(ApiResponse.success("All dynamic threat rules retrieved.", ruleRepository.findAllByOrderByCategoryAscCreatedAtDesc()));
    }

    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<com.authentica.model.HeuristicRule>> createRule(
            @RequestBody Map<String, Object> body) {
        String categoryStr = (String) body.get("category");
        String pattern = (String) body.get("pattern");
        Integer weight = body.get("weight") != null ? Integer.parseInt(body.get("weight").toString()) : 15;
        String description = (String) body.get("description");

        if (categoryStr == null || pattern == null || pattern.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Category and pattern are required."));
        }

        com.authentica.model.enums.RuleCategory category;
        try {
            category = com.authentica.model.enums.RuleCategory.valueOf(categoryStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid rule category: " + categoryStr));
        }

        com.authentica.model.HeuristicRule rule = new com.authentica.model.HeuristicRule(category, pattern.trim().toLowerCase(), weight, description);
        com.authentica.model.HeuristicRule saved = ruleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Dynamic threat rule added successfully.", saved));
    }

    @PatchMapping("/rules/{id}/toggle")
    public ResponseEntity<ApiResponse<com.authentica.model.HeuristicRule>> toggleRule(@PathVariable Long id) {
        com.authentica.model.HeuristicRule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Rule not found with id: " + id));
        }
        rule.setIsActive(!rule.getIsActive());
        com.authentica.model.HeuristicRule updated = ruleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success("Rule active status toggled.", updated));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        if (!ruleRepository.existsById(id)) {
            return ResponseEntity.status(404).body(ApiResponse.error("Rule not found with id: " + id));
        }
        ruleRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Dynamic threat rule deleted successfully.", null));
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

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long id) {
        try {
            communityReportService.deleteReport(id);
            return ResponseEntity.ok(ApiResponse.success("Report deleted successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.error("Failed to delete report: " + e.getMessage()));
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
