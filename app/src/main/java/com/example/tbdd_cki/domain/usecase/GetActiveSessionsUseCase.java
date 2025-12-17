package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.AttendanceSession;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import javax.inject.Inject;

public class GetActiveSessionsUseCase {
    private final AttendanceRepository attendanceRepository;

    @Inject
    public GetActiveSessionsUseCase(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Observable<List<AttendanceSession>> execute(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return Observable.error(
                new IllegalArgumentException("Student ID cannot be empty")
            );
        }

        return attendanceRepository.getActiveSessionsForStudent(studentId);
    }
}
