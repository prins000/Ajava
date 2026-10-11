package com.authentica.dto;

import com.authentica.model.Analysis;
import com.authentica.model.enums.AnalysisType;

import java.util.List;

public class AnalysisDto {

    public static class ScanRequest {
        private AnalysisType type;
        private String target; // URL, phone number, SMS text, or QR content
        private String senderNumber; // optional for SMS
        private Integer durationSeconds; // optional for voice
        private String transcript; // optional for voice

        public ScanRequest() {}

        public AnalysisType getType() { return type; }
        public void setType(AnalysisType type) { this.type = type; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getSenderNumber() { return senderNumber; }
        public void setSenderNumber(String senderNumber) { this.senderNumber = senderNumber; }
        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
        public String getTranscript() { return transcript; }
        public void setTranscript(String transcript) { this.transcript = transcript; }
    }

    public static class ScanResponse {
        private Analysis analysis;
        private List<String> flags;
        private List<String> recommendations;

        public ScanResponse(Analysis analysis, List<String> flags, List<String> recommendations) {
            this.analysis = analysis;
            this.flags = flags;
            this.recommendations = recommendations;
        }

        public Analysis getAnalysis() { return analysis; }
        public void setAnalysis(Analysis analysis) { this.analysis = analysis; }
        public List<String> getFlags() { return flags; }
        public void setFlags(List<String> flags) { this.flags = flags; }
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
}
