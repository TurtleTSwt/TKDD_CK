package com.example.tbdd_cki.data.source.local.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String username;
    public String password;
    public String fullName;
    public String email;
    public String phone;
    public String role;
    public String studentId;
    public long createdAt;
}

