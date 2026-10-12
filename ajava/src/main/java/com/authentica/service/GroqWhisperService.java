package com.authentica.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.Duration;

@Service
public class GroqWhisperService {

    private static final Logger logger = LoggerFactory.getLogger(GroqWhisperService.class);

    @Value("${groq.api.key:}")
    private String groqApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public String transcribeAudio(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("No audio file provided for transcription.");
        }

        if (groqApiKey == null || groqApiKey.isBlank()) {
            logger.warn("Groq API key is not configured.");
            return "Audio received, but Groq API key is not configured for automatic transcription.";
        }

        try {
            String boundary = "----GroqWhisperBoundary" + System.currentTimeMillis();
            byte[] fileBytes = audioFile.getBytes();
            String originalFilename = audioFile.getOriginalFilename() != null ? audioFile.getOriginalFilename() : "recording.wav";

            StringBuilder header = new StringBuilder();
            // model field
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"model\"\r\n\r\n");
            header.append("whisper-large-v3\r\n");

            // temperature field
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"temperature\"\r\n\r\n");
            header.append("0.0\r\n");

            // response_format field
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"response_format\"\r\n\r\n");
            header.append("json\r\n");

            // file field
            header.append("--").append(boundary).append("\r\n");
            header.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(originalFilename).append("\"\r\n");
            header.append("Content-Type: ").append(audioFile.getContentType() != null ? audioFile.getContentType() : "audio/wav").append("\r\n\r\n");

            byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
            byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

            byte[] fullBody = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
            System.arraycopy(headerBytes, 0, fullBody, 0, headerBytes.length);
            System.arraycopy(fileBytes, 0, fullBody, headerBytes.length, fileBytes.length);
            System.arraycopy(footerBytes, 0, fullBody, headerBytes.length + fileBytes.length, footerBytes.length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/audio/transcriptions"))
                    .header("Authorization", "Bearer " + groqApiKey.trim())
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .timeout(Duration.ofSeconds(45))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("text")) {
                    return root.get("text").asText();
                }
                return response.body();
            } else {
                logger.error("Groq Whisper API returned error status {}: {}", response.statusCode(), response.body());
                return "Transcription failed (HTTP " + response.statusCode() + "). Audio file may be corrupt or format unsupported.";
            }
        } catch (Exception e) {
            logger.error("Exception invoking Groq Whisper API: {}", e.getMessage(), e);
            return "Transcription error: " + e.getMessage();
        }
    }
}
