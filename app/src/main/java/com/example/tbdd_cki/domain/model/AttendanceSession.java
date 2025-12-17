package com.example.tbdd_cki.domain.model;

import java.util.Date;

public class AttendanceSession {
    private String id;
    private String classId;
    private String className;
    private Date sessionDate;
    private String sessionTime;
    private String qrCode;
    private Date qrExpiredAt;
    private SessionStatus status;
    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private Date createdAt;
    private Date updatedAt;

    public AttendanceSession(String id, String classId, String className, Date sessionDate,
                             String sessionTime, String qrCode, Date qrExpiredAt,
                             SessionStatus status, int totalStudents, int presentCount,
                             int absentCount, Date createdAt, Date updatedAt) {
        this.id = id;
        this.classId = classId;
        this.className = className;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.qrCode = qrCode;
        this.qrExpiredAt = qrExpiredAt;
        this.status = status;
        this.totalStudents = totalStudents;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }

    public String getSessionTime() { return sessionTime; }
    public void setSessionTime(String sessionTime) { this.sessionTime = sessionTime; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public Date getQrExpiredAt() { return qrExpiredAt; }
    public void setQrExpiredAt(Date qrExpiredAt) { this.qrExpiredAt = qrExpiredAt; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}