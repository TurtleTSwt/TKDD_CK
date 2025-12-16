package com.example.tbdd_cki.data.source.local.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tbdd_cki.data.source.local.database.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    UserEntity login(String username, String password);

    @Query("SELECT * FROM users WHERE id = :id")
    UserEntity getUserById(long id);

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    List<UserEntity> getAllStudents();

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserEntity findByUsername(String username);
}
