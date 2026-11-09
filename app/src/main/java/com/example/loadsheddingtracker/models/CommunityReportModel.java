package com.example.loadsheddingtracker.models;

// LECTURE TOPIC: Firebase Realtime Database Model
// Requirements:
// 1. Public no-argument constructor (required for DataSnapshot.getValue(CommunityReportModel.class))
// 2. Public getters and setters for all serialized properties
public class CommunityReportModel {
    private String reportId;
    private String area;
    private String cause;
    private String description;
    private String reporterEmail;
    private String userId;
    private long timestamp;

    // MANDATORY for Firebase Realtime Database deserialization
    public CommunityReportModel() {}

    public CommunityReportModel(String reportId, String area, String cause, String description,
                                String reporterEmail, String userId, long timestamp) {
        this.reportId = reportId;
        this.area = area;
        this.cause = cause;
        this.description = description;
        this.reporterEmail = reporterEmail;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

