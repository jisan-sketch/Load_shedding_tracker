package com.example.loadsheddingtracker.models;

// LECTURE TOPIC: Data Model - Schedule Model
// Represents a single load-shedding schedule time slot for an area.
public class ScheduleModel {
    private long id;
    private String area;
    private String startTime;
    private String endTime;
    private String status;
    private String dayOfWeek;

    // Required empty constructor
    public ScheduleModel() {}

    public ScheduleModel(long id, String area, String startTime, String endTime, String status, String dayOfWeek) {
        this.id = id;
        this.area = area;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.dayOfWeek = dayOfWeek;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getTimeRangeFormatted() {
        return startTime + " - " + endTime;
    }
}

