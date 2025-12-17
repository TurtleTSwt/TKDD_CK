package com.example.tbdd_cki.domain.model;

public class ClassSchedule {
    private int dayOfWeek; // 1=Monday, 7=Sunday
    private String startTime; // "08:00"
    private String endTime; // "09:30"
    private String room;

    public ClassSchedule(int dayOfWeek, String startTime, String endTime, String room) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    // Getters and Setters
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}