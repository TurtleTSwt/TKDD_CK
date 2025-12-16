package com.example.tbdd_cki.domain.model;

public class AttendanceStats {
    private int totalSessions;
    private int presentCount;
    private int lateCount;
    private int absentCount;
    private float attendanceRate; // percentage

    public AttendanceStats(int totalSessions, int presentCount, int lateCount,
                          int absentCount, float attendanceRate) {
        this.totalSessions = totalSessions;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.attendanceRate = attendanceRate;
    }

    // Getters and Setters
    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    
    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
    
    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }
    
    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
    
    public float getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(float attendanceRate) { this.attendanceRate = attendanceRate; }
}