package com.example.tbdd_cki.data.source.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance")
public class AttendanceEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long sessionId;
    public long studentId;
    public String studentName;
    public String studentCode;
    public long attendanceTime; // timestamp
    public String method; // "QR_CODE", "GPS", "MANUAL"
    public double latitude;
    public double longitude;
    public String status; // "PRESENT", "LATE", "ABSENT"
    public String note;
    public long createdAt;
}
