package com.example.tbdd_cki.data.repository;

import android.content.Context;
import android.location.Location;
import com.example.tbdd_cki.data.source.local.database.AppDatabase;
import com.example.tbdd_cki.data.source.local.database.entity.*;
import com.example.tbdd_cki.domain.model.*;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.*;

public class AttendanceRepositoryImpl implements AttendanceRepository {
    private final AppDatabase database;

    public AttendanceRepositoryImpl(Context context) {
        this.database = AppDatabase.getInstance(context);
    }

    @Override
    public Single<Result<AttendanceSession>> createSession(String classId, long sessionDate,
                                                           String sessionTime, int qrExpirationMinutes) {
        return Single.fromCallable(() -> {
            ClassEntity classEntity = database.classDao().getById(Long.parseLong(classId));
            if (classEntity == null) {
                return Result.<AttendanceSession>error(
                    new Exception("Class not found"),
                    "Class not found"
                );
            }

            SessionEntity session = new SessionEntity();
            session.classId = Long.parseLong(classId);
            session.className = classEntity.className;
            session.sessionDate = sessionDate;
            session.sessionTime = sessionTime;
            session.qrCode = generateQRCode();
            session.qrExpiredAt = System.currentTimeMillis() + (qrExpirationMinutes * 60 * 1000);
            session.status = "ACTIVE";
            session.createdAt = System.currentTimeMillis();

            long id = database.sessionDao().insert(session);
            session.id = id;

            AttendanceSession result = mapToAttendanceSession(session);
            return Result.success(result);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<AttendanceSession>> getSessionById(String sessionId) {
        return Single.fromCallable(() -> {
            SessionEntity entity = database.sessionDao().getById(Long.parseLong(sessionId));
            if (entity == null) {
                return Result.<AttendanceSession>error(
                    new Exception("Session not found"),
                    "Session not found"
                );
            }

            AttendanceSession session = mapToAttendanceSession(entity);
            return Result.success(session);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<AttendanceSession>> getActiveSessionsForStudent(String studentId) {
        return Observable.fromCallable(() -> {
            List<SessionEntity> entities = database.sessionDao().getActiveSessions();
            List<AttendanceSession> sessions = new ArrayList<>();
            for (SessionEntity entity : entities) {
                sessions.add(mapToAttendanceSession(entity));
            }
            return sessions;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<AttendanceSession>> getSessionsByClass(String classId) {
        return Observable.fromCallable(() -> {
            List<SessionEntity> entities = database.sessionDao()
                .getByClass(Long.parseLong(classId));
            List<AttendanceSession> sessions = new ArrayList<>();
            for (SessionEntity entity : entities) {
                sessions.add(mapToAttendanceSession(entity));
            }
            return sessions;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<Void>> closeSession(String sessionId) {
        return Single.fromCallable(() -> {
            database.sessionDao().closeSession(Long.parseLong(sessionId));
            return Result.<Void>success(null);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<AttendanceRecord>> checkIn(CheckInRequest request) {
        return Single.fromCallable(() -> {
            AttendanceEntity existing = database.attendanceDao()
                .checkExists(Long.parseLong(request.getSessionId()),
                           Long.parseLong(request.getStudentId()));

            if (existing != null) {
                return Result.<AttendanceRecord>error(
                    new Exception("Already checked in"),
                    "You have already checked in for this session"
                );
            }

            UserEntity student = database.userDao()
                .getUserById(Long.parseLong(request.getStudentId()));

            AttendanceEntity attendance = new AttendanceEntity();
            attendance.sessionId = Long.parseLong(request.getSessionId());
            attendance.studentId = Long.parseLong(request.getStudentId());
            attendance.studentName = student.fullName;
            attendance.studentCode = student.studentId;
            attendance.attendanceTime = System.currentTimeMillis();
            attendance.method = request.getMethod().name();
            attendance.latitude = request.getLatitude();
            attendance.longitude = request.getLongitude();
            attendance.status = "PRESENT";
            attendance.createdAt = System.currentTimeMillis();

            long id = database.attendanceDao().insert(attendance);
            attendance.id = id;

            AttendanceRecord record = mapToAttendanceRecord(attendance);
            return Result.success(record);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<AttendanceRecord>> getAttendanceRecordsBySession(String sessionId) {
        return Observable.fromCallable(() -> {
            List<AttendanceEntity> entities = database.attendanceDao()
                .getBySession(Long.parseLong(sessionId));
            List<AttendanceRecord> records = new ArrayList<>();
            for (AttendanceEntity entity : entities) {
                records.add(mapToAttendanceRecord(entity));
            }
            return records;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<AttendanceRecord>> getAttendanceRecordsByStudent(String studentId) {
        return Observable.fromCallable(() -> {
            List<AttendanceEntity> entities = database.attendanceDao()
                .getByStudent(Long.parseLong(studentId));
            List<AttendanceRecord> records = new ArrayList<>();
            for (AttendanceEntity entity : entities) {
                records.add(mapToAttendanceRecord(entity));
            }
            return records;
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<AttendanceStats>> getAttendanceStats(String studentId, String classId) {
        return Single.fromCallable(() -> {
            int presentCount = database.attendanceDao()
                .getPresentCount(Long.parseLong(studentId));
            int absentCount = database.attendanceDao()
                .getAbsentCount(Long.parseLong(studentId));
            int total = presentCount + absentCount;
            float rate = total > 0 ? (presentCount * 100f / total) : 0f;

            AttendanceStats stats = new AttendanceStats(
                total, presentCount, 0, absentCount, rate
            );
            return Result.success(stats);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<AttendanceRecord>> updateAttendanceStatus(String recordId,
                                                                   AttendanceStatus status,
                                                                   String note) {
        return Single.fromCallable(() -> {
            return Result.<AttendanceRecord>success(null);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<Boolean>> verifyQRCode(String qrCode, String sessionId) {
        return Single.fromCallable(() -> {
            SessionEntity session = database.sessionDao()
                .getById(Long.parseLong(sessionId));

            if (session == null) {
                return Result.success(false);
            }

            boolean valid = session.qrCode.equals(qrCode) &&
                          System.currentTimeMillis() < session.qrExpiredAt;

            return Result.success(valid);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<Boolean>> verifyLocation(String sessionId, double latitude,
                                                  double longitude) {
        return Single.fromCallable(() -> {
            SessionEntity session = database.sessionDao()
                .getById(Long.parseLong(sessionId));

            if (session == null) {
                return Result.success(false);
            }

            ClassEntity classEntity = database.classDao().getById(session.classId);

            float distance = calculateDistance(
                latitude, longitude,
                classEntity.latitude, classEntity.longitude
            );

            boolean valid = distance <= classEntity.allowedRadius;
            return Result.success(valid);
        }).subscribeOn(Schedulers.io());
    }

    private String generateQRCode() {
        return "QR_" + UUID.randomUUID().toString();
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        return loc1.distanceTo(loc2);
    }

    private AttendanceSession mapToAttendanceSession(SessionEntity entity) {
        return new AttendanceSession(
            String.valueOf(entity.id),
            String.valueOf(entity.classId),
            entity.className,
            new Date(entity.sessionDate),
            entity.sessionTime,
            entity.qrCode,
            new Date(entity.qrExpiredAt),
            SessionStatus.valueOf(entity.status),
            0, 0, 0,
            new Date(entity.createdAt),
            new Date(entity.createdAt)
        );
    }

    private AttendanceRecord mapToAttendanceRecord(AttendanceEntity entity) {
        return new AttendanceRecord(
            String.valueOf(entity.id),
            String.valueOf(entity.sessionId),
            String.valueOf(entity.studentId),
            entity.studentName,
            entity.studentCode,
            new Date(entity.attendanceTime),
            AttendanceMethod.valueOf(entity.method),
            new AttendanceLocation(entity.latitude, entity.longitude, null),
            AttendanceStatus.valueOf(entity.status),
            entity.note,
            new Date(entity.createdAt)
        );
    }
}
