package com.authentica.controller;

import com.authentica.dto.ApiResponse;
import com.authentica.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/media")
public class MediaUploadController {

    private final CloudinaryService cloudinaryService;

    public MediaUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is empty."));
        }

        try {
            String uploadedUrl = cloudinaryService.uploadFile(file);
            Map<String, String> data = Map.of(
                    "url", uploadedUrl,
                    "filename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "file",
                    "contentType", file.getContentType() != null ? file.getContentType() : "application/octet-stream"
            );
            return ResponseEntity.ok(ApiResponse.success("Media uploaded successfully.", data));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Upload failed: " + e.getMessage()));
        }
    }
}
