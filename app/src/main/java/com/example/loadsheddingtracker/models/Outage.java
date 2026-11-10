package com.example.loadsheddingtracker.models;

import com.google.firebase.firestore.Exclude;

/**
 * LECTURE TOPIC: Cloud Firestore Document Model (Outage History)
 * Requirements:
 * 1. Public fields: id, area, startTime, endTime, userId as String; durationMinutes as int; timestamp as long
 * 2. Public empty constructor (MANDATORY for Firestore doc.toObject(Outage.class))
 * 3. Full constructor
 */
public class Outage {
    @Exclude
    public String id;

    public String area;
    public String startTime;
    public String endTime;
    public String userId;
    public int durationMinutes;
    public long timestamp;
    public String notes;

    // MANDATORY empty constructor for Firestore document deserialization
    public Outage() {
    }

    public Outage(String id, String area, String startTime, String endTime, String userId, int durationMinutes, long timestamp) {
        this.id = id;
        this.area = area;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
        this.durationMinutes = durationMinutes;
        this.timestamp = timestamp;
        this.notes = "";
    }

    public Outage(String id, String area, String startTime, String endTime, String userId, int durationMinutes, long timestamp, String notes) {
        this.id = id;
        this.area = area;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
        this.durationMinutes = durationMinutes;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    // Standard getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNotes() { return notes != null ? notes : ""; }
    public void setNotes(String notes) { this.notes = notes; }
}

