package com.authentica.controller;

import com.authentica.dto.AnalysisDto;
import com.authentica.dto.ApiResponse;
import com.authentica.model.Analysis;
import com.authentica.model.User;
import com.authentica.repository.UserRepository;
import com.authentica.service.AnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final UserRepository userRepository;

    public AnalysisController(AnalysisService analysisService, UserRepository userRepository) {
        this.analysisService = analysisService;
        this.userRepository = userRepository;
    }

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<AnalysisDto.ScanResponse>> scan(@RequestBody AnalysisDto.ScanRequest request,
                                                                      Authentication authentication) {
        Long userId = null;
        if (authentication != null) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) userId = user.getId();
        }

        AnalysisDto.ScanResponse response = analysisService.performScan(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Threat analysis completed.", response));
    }

    @PostMapping("/scan-public")
    public ResponseEntity<ApiResponse<AnalysisDto.ScanResponse>> scanPublic(@RequestBody AnalysisDto.ScanRequest request) {
        AnalysisDto.ScanResponse response = analysisService.performScan(null, request);
        return ResponseEntity.ok(ApiResponse.success("Threat analysis completed (Public).", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<Analysis>>> getHistory(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
        }

        Page<Analysis> history = analysisService.getUserHistory(user.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success("Analysis history retrieved.", history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Analysis>> getDetail(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
        }

        return analysisService.getDetail(id, user.getId())
                .map(a -> ResponseEntity.ok(ApiResponse.success("Analysis entry found.", a)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Analysis record not found.")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEntry(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("User not found"));
        }

        analysisService.archiveScan(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Analysis entry archived successfully.", "OK"));
    }
}
