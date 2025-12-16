package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.AttendanceStats;
import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class GetAttendanceStatsUseCase {
    private final AttendanceRepository attendanceRepository;

    @Inject
    public GetAttendanceStatsUseCase(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Single<Result<AttendanceStats>> execute(String studentId, String classId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Student ID cannot be empty"),
                "Student ID cannot be empty"
            ));
        }

        if (classId == null || classId.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Class ID cannot be empty"),
                "Class ID cannot be empty"
            ));
        }

        return attendanceRepository.getAttendanceStats(studentId, classId);
    }
}
