package com.example.tbdd_cki.data.source.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class ClassEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String classCode;
    public String className;
    public String subjectName;
    public long teacherId;
    public String teacherName;
    public String semester;
    public String academicYear;
    public double latitude;
    public double longitude;
    public int allowedRadius; // meters
    public long createdAt;
}
