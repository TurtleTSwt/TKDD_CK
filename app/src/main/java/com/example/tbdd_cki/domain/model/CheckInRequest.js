package com.example.tbdd_cki.domain.model;

public class CheckInRequest {
    private String sessionId;
    private String studentId;
    private String qrCode;
    private double latitude;
    private double longitude;
    private AttendanceMethod method;

    public CheckInRequest(String sessionId, String studentId, String qrCode,
                         double latitude, double longitude, AttendanceMethod method) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.qrCode = qrCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.method = method;
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public AttendanceMethod getMethod() { return method; }
    public void setMethod(AttendanceMethod method) { this.method = method; }
}