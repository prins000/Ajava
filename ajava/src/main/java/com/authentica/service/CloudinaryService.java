package com.authentica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.UUID;

@Service
public class CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file.");
        }

        if (cloudName == null || cloudName.isBlank() || apiKey == null || apiKey.isBlank()) {
            logger.warn("Cloudinary credentials not configured. Returning fallback placeholder.");
            return "/images/evidence-" + UUID.randomUUID() + ".png";
        }

        try {
            long timestamp = System.currentTimeMillis() / 1000L;
            String toSign = "timestamp=" + timestamp + apiSecret;
            String signature = sha1Hex(toSign);

            String boundary = "----AuthenticaBoundary" + System.currentTimeMillis();
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "evidence.png";

            StringBuilder header = new StringBuilder();
            // api_key
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"api_key\"\r\n\r\n");
            header.append(apiKey).append("\r\n");

            // timestamp
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"timestamp\"\r\n\r\n");
            header.append(timestamp).append("\r\n");

            // signature
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"signature\"\r\n\r\n");
            header.append(signature).append("\r\n");

            // file header
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(originalFilename).append("\"\r\n");
            header.append("Content-Type: ").append(file.getContentType() != null ? file.getContentType() : "application/octet-stream").append("\r\n\r\n");

            byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
            byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

            byte[] fullBody = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
            System.arraycopy(headerBytes, 0, fullBody, 0, headerBytes.length);
            System.arraycopy(fileBytes, 0, fullBody, headerBytes.length, fileBytes.length);
            System.arraycopy(footerBytes, 0, fullBody, headerBytes.length + fileBytes.length, footerBytes.length);

            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName.trim() + "/auto/upload";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String body = response.body();
                // Simple JSON extraction for secure_url
                int urlIdx = body.indexOf("\"secure_url\":\"");
                if (urlIdx != -1) {
                    int start = urlIdx + 14;
                    int end = body.indexOf("\"", start);
                    if (end != -1) {
                        return body.substring(start, end).replace("\\/", "/");
                    }
                }
                return "https://res.cloudinary.com/" + cloudName + "/image/upload/" + originalFilename;
            } else {
                logger.error("Cloudinary upload failed with status {}: {}", response.statusCode(), response.body());
                return "/images/evidence-" + System.currentTimeMillis() + ".png";
            }
        } catch (Exception e) {
            logger.error("Error uploading to Cloudinary: {}", e.getMessage(), e);
            return "/images/evidence-" + System.currentTimeMillis() + ".png";
        }
    }

    private String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
