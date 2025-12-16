package com.example.tbdd_cki.data.source.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class SessionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long classId;
    public String className;
    public long sessionDate; // timestamp
    public String sessionTime; // "08:00"
    public String qrCode;
    public long qrExpiredAt; // timestamp
    public String status; // "ACTIVE", "EXPIRED", "CLOSED"
    public long createdAt;
}
