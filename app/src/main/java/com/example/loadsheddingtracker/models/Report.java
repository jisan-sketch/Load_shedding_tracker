package com.example.loadsheddingtracker.models;

/**
 * LECTURE TOPIC: Firebase Realtime Database Model (Community Reports)
 * Requirements:
 * 1. Public fields: id, area, cause, description, userId (String) and time (long)
 * 2. Public empty constructor (MANDATORY for Firebase child.getValue(Report.class))
 * 3. Full constructor
 */
public class Report {
    public String id;
    public String area;
    public String cause;
    public String description;
    public String userId;
    public long time;

    // MANDATORY empty constructor for Firebase DataSnapshot.getValue(Report.class)
    public Report() {
    }

    // Full constructor
    public Report(String id, String area, String cause, String description, String userId, long time) {
        this.id = id;
        this.area = area;
        this.cause = cause;
        this.description = description;
        this.userId = userId;
        this.time = time;
    }

    // Standard getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
}

