package com.authentica.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OutboundProbeService {

    private static final Logger logger = LoggerFactory.getLogger(OutboundProbeService.class);

    @Value("${virustotal.api.key:}")
    private String virusTotalApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static class ProbeResult {
        private int httpStatus = 0;
        private String sslIssuer = null;
        private Long certAgeDays = null;
        private int vtMaliciousCount = 0;
        private int threatScorePenalty = 0;
        private List<String> flags = new ArrayList<>();

        public int getHttpStatus() { return httpStatus; }
        public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }
        public String getSslIssuer() { return sslIssuer; }
        public void setSslIssuer(String sslIssuer) { this.sslIssuer = sslIssuer; }
        public Long getCertAgeDays() { return certAgeDays; }
        public void setCertAgeDays(Long certAgeDays) { this.certAgeDays = certAgeDays; }
        public int getVtMaliciousCount() { return vtMaliciousCount; }
        public void setVtMaliciousCount(int vtMaliciousCount) { this.vtMaliciousCount = vtMaliciousCount; }
        public int getThreatScorePenalty() { return threatScorePenalty; }
        public void setThreatScorePenalty(int threatScorePenalty) { this.threatScorePenalty = threatScorePenalty; }
        public List<String> getFlags() { return flags; }
        public void setFlags(List<String> flags) { this.flags = flags; }
    }

    public ProbeResult probe(String rawUrl, String host) {
        ProbeResult result = new ProbeResult();

        // 1. Live SSL & HTTP Sandbox Probe
        probeSslAndHttp(rawUrl, result);

        // 2. VirusTotal Domain Intelligence Check
        probeVirusTotal(host, result);

        return result;
    }

    private void probeSslAndHttp(String rawUrl, ProbeResult result) {
        try {
            String fullUrl = rawUrl.startsWith("http://") || rawUrl.startsWith("https://") ? rawUrl : "https://" + rawUrl;
            URL url = new URI(fullUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(3500);
            conn.setReadTimeout(3500);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Authentica-Threat-Probe/1.0");

            int code = conn.getResponseCode();
            result.setHttpStatus(code);

            if (conn instanceof HttpsURLConnection httpsConn) {
                try {
                    Certificate[] certs = httpsConn.getServerCertificates();
                    if (certs != null && certs.length > 0 && certs[0] instanceof X509Certificate x509) {
                        String issuer = x509.getIssuerX500Principal().getName();
                        result.setSslIssuer(cleanIssuer(issuer));

                        Date issuedDate = x509.getNotBefore();
                        long ageDays = ChronoUnit.DAYS.between(issuedDate.toInstant(), new Date().toInstant());
                        result.setCertAgeDays(ageDays);

                        // If certificate is extremely young (< 14 days old), high phishing indicator
                        if (ageDays >= 0 && ageDays < 14) {
                            result.getFlags().add("Ephemeral SSL certificate detected: Issued only " + ageDays + " days ago (common in zero-day phishing)");
                            result.setThreatScorePenalty(result.getThreatScorePenalty() + 25);
                        } else {
                            result.getFlags().add("SSL Certificate Authority: " + result.getSslIssuer() + " (Age: " + ageDays + " days)");
                        }
                    }
                } catch (Exception sslEx) {
                    logger.debug("Could not read peer SSL certificates: {}", sslEx.getMessage());
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            logger.debug("Outbound probe connection note for {}: {}", rawUrl, e.getMessage());
        }
    }

    private void probeVirusTotal(String host, ProbeResult result) {
        if (virusTotalApiKey == null || virusTotalApiKey.isBlank() || host == null || host.isBlank()) {
            return;
        }

        // Clean host from port or IP
        String cleanHost = host.trim().toLowerCase();
        if (cleanHost.contains(":")) {
            cleanHost = cleanHost.substring(0, cleanHost.indexOf(':'));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.virustotal.com/api/v3/domains/" + cleanHost))
                    .header("x-apikey", virusTotalApiKey.trim())
                    .timeout(Duration.ofSeconds(4))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode stats = root.path("data").path("attributes").path("last_analysis_stats");
                if (!stats.isMissingNode()) {
                    int malicious = stats.path("malicious").asInt(0);
                    int suspicious = stats.path("suspicious").asInt(0);
                    result.setVtMaliciousCount(malicious);

                    if (malicious > 0) {
                        result.getFlags().add("VirusTotal Global Threat Intelligence: Flagged MALICIOUS by " + malicious + " security vendor(s)");
                        result.setThreatScorePenalty(result.getThreatScorePenalty() + Math.min(60, malicious * 20));
                    } else if (suspicious > 0) {
                        result.getFlags().add("VirusTotal Global Threat Intelligence: Flagged suspicious by " + suspicious + " security vendor(s)");
                        result.setThreatScorePenalty(result.getThreatScorePenalty() + 15);
                    } else {
                        result.getFlags().add("VirusTotal Global Threat Intelligence: Clean reputation across security scanners");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("VirusTotal probe exception for {}: {}", host, e.getMessage());
        }
    }

    private String cleanIssuer(String dn) {
        if (dn == null) return "Unknown";
        for (String part : dn.split(",")) {
            part = part.trim();
            if (part.startsWith("CN=")) {
                return part.substring(3);
            }
            if (part.startsWith("O=")) {
                return part.substring(2);
            }
        }
        return dn;
    }
}
