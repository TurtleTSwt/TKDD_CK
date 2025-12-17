package com.example.tbdd_cki.domain.usecase;

import android.location.Location;
import com.example.tbdd_cki.domain.model.*;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class CheckInUseCase {
    private final AttendanceRepository attendanceRepository;
    private final LocationValidator locationValidator;

    @Inject
    public CheckInUseCase(AttendanceRepository attendanceRepository,
                         LocationValidator locationValidator) {
        this.attendanceRepository = attendanceRepository;
        this.locationValidator = locationValidator;
    }

    public Single<Result<AttendanceRecord>> execute(CheckInRequest request) {
        return locationValidator.validate(request.getSessionId(), 
                                        request.getLatitude(), 
                                        request.getLongitude())
            .flatMap(isValid -> {
                if (!isValid) {
                    return Single.just(Result.<AttendanceRecord>error(
                        new IllegalStateException("You are not within the allowed location"),
                        "You are not within the allowed location"
                    ));
                }
                return attendanceRepository.checkIn(request);
            });
    }
}
