package com.example.loadsheddingtracker.models;

// LECTURE TOPIC: Cloud Firestore Document Model
// Represents historical load-shedding incidents stored in the 'outage_history' collection.
public class OutageHistoryModel {
    private String documentId;
    private String area;
    private String startTime;
    private String endTime;
    private int durationMinutes;
    private String userId;
    private String userEmail;
    private String notes;
    private long timestamp;

    // Public no-arg constructor required for Firestore document mapping
    public OutageHistoryModel() {}

    public OutageHistoryModel(String documentId, String area, String startTime, String endTime,
                              int durationMinutes, String userId, String userEmail, String notes, long timestamp) {
        this.documentId = documentId;
        this.area = area;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.userId = userId;
        this.userEmail = userEmail;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

