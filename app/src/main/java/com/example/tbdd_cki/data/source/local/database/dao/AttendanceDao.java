package com.example.tbdd_cki.data.source.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.tbdd_cki.data.source.local.database.entity.AttendanceEntity;
import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert
    long insert(AttendanceEntity attendance);

    @Update
    void update(AttendanceEntity attendance);

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId")
    List<AttendanceEntity> getBySession(long sessionId);

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY attendanceTime DESC")
    List<AttendanceEntity> getByStudent(long studentId);

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'PRESENT'")
    int getPresentCount(long studentId);

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'ABSENT'")
    int getAbsentCount(long studentId);

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId AND studentId = :studentId LIMIT 1")
    AttendanceEntity checkExists(long sessionId, long studentId);
}
