package com.example.tbdd_cki.data.source.local.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.tbdd_cki.data.source.local.database.entity.ClassEntity;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    long insert(ClassEntity classEntity);

    @Update
    void update(ClassEntity classEntity);

    @Delete
    void delete(ClassEntity classEntity);

    @Query("SELECT * FROM classes WHERE id = :id")
    ClassEntity getById(long id);

    @Query("SELECT * FROM classes WHERE teacherId = :teacherId")
    List<ClassEntity> getByTeacher(long teacherId);

    @Query("SELECT * FROM classes")
    List<ClassEntity> getAll();
}
