package com.authentica.dto;

public class CommunityReportDto {

    public static class CreateRequest {
        private String type;
        private String title;
        private String description;
        private String targetIdentifier;
        private String additionalInfo;
        private String tags;
        private String city;
        private String state;
        private String reporterName;

        public CreateRequest() {}

        public String getReporterName() { return reporterName; }
        public void setReporterName(String reporterName) { this.reporterName = reporterName; }
        public void setAuthorName(String authorName) { this.reporterName = authorName; }
        public String getAuthorName() { return reporterName; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTargetIdentifier() { return targetIdentifier; }
        public void setTargetIdentifier(String targetIdentifier) { this.targetIdentifier = targetIdentifier; }
        public String getAdditionalInfo() { return additionalInfo; }
        public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}
