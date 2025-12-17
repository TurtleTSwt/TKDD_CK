package com.example.tbdd_cki.domain.model;

import java.util.Date;
import java.util.List;

public class ClassModel {
    private String id;
    private String classCode;
    private String className;
    private String subjectName;
    private String teacherId;
    private String teacherName;
    private List<ClassSchedule> schedule;
    private String semester;
    private String academicYear;
    private ClassLocation location;
    private int totalStudents;
    private Date createdAt;
    private Date updatedAt;

    public ClassModel(String id, String classCode, String className, String subjectName,
                      String teacherId, String teacherName, List<ClassSchedule> schedule,
                      String semester, String academicYear, ClassLocation location,
                      int totalStudents, Date createdAt, Date updatedAt) {
        this.id = id;
        this.classCode = classCode;
        this.className = className;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.schedule = schedule;
        this.semester = semester;
        this.academicYear = academicYear;
        this.location = location;
        this.totalStudents = totalStudents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public List<ClassSchedule> getSchedule() { return schedule; }
    public void setSchedule(List<ClassSchedule> schedule) { this.schedule = schedule; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public ClassLocation getLocation() { return location; }
    public void setLocation(ClassLocation location) { this.location = location; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}