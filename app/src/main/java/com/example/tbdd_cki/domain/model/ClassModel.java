package com.example.tbdd_cki.domain.model;

public class ClassModel {
    private String id;
    private String name;
    private String code;
    private String description;
    private String teacherId;
    private boolean isActive;
    private int studentCount;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public ClassModel() {
    }

    public ClassModel(String id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.isActive = true;
        this.studentCount = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", studentCount=" + studentCount +
                ", isActive=" + isActive +
                '}';
    }
}