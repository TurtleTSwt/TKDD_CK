package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.AttendanceRecord;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import javax.inject.Inject;

public class GetAttendanceHistoryUseCase {
    private final AttendanceRepository attendanceRepository;

    @Inject
    public GetAttendanceHistoryUseCase(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Observable<List<AttendanceRecord>> execute(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return Observable.error(
                new IllegalArgumentException("Student ID cannot be empty")
            );
        }

        return attendanceRepository.getAttendanceRecordsByStudent(studentId);
    }
}
