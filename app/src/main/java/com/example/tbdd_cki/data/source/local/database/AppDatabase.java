package com.example.tbdd_cki.data.source.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tbdd_cki.data.source.local.database.dao.AttendanceDao;
import com.example.tbdd_cki.data.source.local.database.dao.ClassDao;
import com.example.tbdd_cki.data.source.local.database.dao.SessionDao;
import com.example.tbdd_cki.data.source.local.database.dao.UserDao;
import com.example.tbdd_cki.data.source.local.database.entity.AttendanceEntity;
import com.example.tbdd_cki.data.source.local.database.entity.ClassEntity;
import com.example.tbdd_cki.data.source.local.database.entity.SessionEntity;
import com.example.tbdd_cki.data.source.local.database.entity.UserEntity;

@Database(entities = {
        UserEntity.class,
        ClassEntity.class,
        SessionEntity.class,
        AttendanceEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();

    public abstract ClassDao classDao();

    public abstract SessionDao sessionDao();

    public abstract AttendanceDao attendanceDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "attendance_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
