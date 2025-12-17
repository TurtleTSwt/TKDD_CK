package com.example.tbdd_cki.domain.repository;

import com.example.tbdd_cki.domain.model.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

public interface AttendanceRepository {
    // Session Management
    Single<Result<AttendanceSession>> createSession(
        String classId,
        long sessionDate,
        String sessionTime,
        int qrExpirationMinutes
    );

    Single<Result<AttendanceSession>> getSessionById(String sessionId);

    Observable<List<AttendanceSession>> getActiveSessionsForStudent(String studentId);

    Observable<List<AttendanceSession>> getSessionsByClass(String classId);

    Single<Result<Void>> closeSession(String sessionId);

    // Attendance Records
    Single<Result<AttendanceRecord>> checkIn(CheckInRequest request);

    Observable<List<AttendanceRecord>> getAttendanceRecordsBySession(String sessionId);

    Observable<List<AttendanceRecord>> getAttendanceRecordsByStudent(String studentId);

    Single<Result<AttendanceStats>> getAttendanceStats(String studentId, String classId);

    Single<Result<AttendanceRecord>> updateAttendanceStatus(
        String recordId,
        AttendanceStatus status,
        String note
    );

    // QR Code
    Single<Result<Boolean>> verifyQRCode(String qrCode, String sessionId);

    // Location
    Single<Result<Boolean>> verifyLocation(
        String sessionId,
        double latitude,
        double longitude
    );
}
