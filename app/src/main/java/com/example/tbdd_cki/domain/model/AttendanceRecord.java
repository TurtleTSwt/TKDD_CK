package com.example.tbdd_cki.domain.model;

import java.util.Date;

public class AttendanceRecord {
    private String id;
    private String sessionId;
    private String studentId;
    private String studentName;
    private String studentCode;
    private Date attendanceTime;
    private AttendanceMethod method;
    private AttendanceLocation location;
    private AttendanceStatus status;
    private String note;
    private Date createdAt;

    public AttendanceRecord(String id, String sessionId, String studentId, String studentName,
                            String studentCode, Date attendanceTime, AttendanceMethod method,
                            AttendanceLocation location, AttendanceStatus status,
                            String note, Date createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.attendanceTime = attendanceTime;
        this.method = method;
        this.location = location;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public Date getAttendanceTime() { return attendanceTime; }
    public void setAttendanceTime(Date attendanceTime) { this.attendanceTime = attendanceTime; }

    public AttendanceMethod getMethod() { return method; }
    public void setMethod(AttendanceMethod method) { this.method = method; }

    public AttendanceLocation getLocation() { return location; }
    public void setLocation(AttendanceLocation location) { this.location = location; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}