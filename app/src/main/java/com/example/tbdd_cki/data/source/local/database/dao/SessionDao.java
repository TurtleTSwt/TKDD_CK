package com.example.tbdd_cki.data.source.local.database.dao;

import androidx.room.Insert;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;
import com.example.tbdd_cki.data.source.local.database.entity.SessionEntity;
import java.util.List;

@Dao
public interface SessionDao {
    @Insert
    long insert(SessionEntity session);

    @Update
    void update(SessionEntity session);

    @Query("SELECT * FROM sessions WHERE id = :id")
    SessionEntity getById(long id);

    @Query("SELECT * FROM sessions WHERE classId = :classId ORDER BY sessionDate DESC")
    List<SessionEntity> getByClass(long classId);

    @Query("SELECT * FROM sessions WHERE status = 'ACTIVE' ORDER BY sessionDate DESC")
    List<SessionEntity> getActiveSessions();

    @Query("UPDATE sessions SET status = 'CLOSED' WHERE id = :id")
    void closeSession(long id);
}
