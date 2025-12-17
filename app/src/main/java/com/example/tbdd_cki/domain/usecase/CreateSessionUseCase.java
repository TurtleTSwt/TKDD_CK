package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.AttendanceSession;
import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class CreateSessionUseCase {
    private final AttendanceRepository attendanceRepository;

    @Inject
    public CreateSessionUseCase(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Single<Result<AttendanceSession>> execute(String classId, 
                                                     long sessionDate,
                                                     String sessionTime,
                                                     int qrExpirationMinutes) {
        if (classId == null || classId.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Class ID cannot be empty"),
                "Class ID cannot be empty"
            ));
        }

        if (qrExpirationMinutes <= 0) {
            return Single.just(Result.error(
                new IllegalArgumentException("QR expiration must be positive"),
                "QR expiration must be positive"
            ));
        }

        return attendanceRepository.createSession(classId, sessionDate, 
                                                 sessionTime, qrExpirationMinutes);
    }
}
