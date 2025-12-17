package com.example.tbdd_cki.domain.usecase;

import android.location.Location;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class LocationValidator {
    private static final int DEFAULT_ALLOWED_RADIUS = 50;

    private final AttendanceRepository attendanceRepository;

    @Inject
    public LocationValidator(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Single<Boolean> validate(String sessionId, double latitude, double longitude) {
        return attendanceRepository.verifyLocation(sessionId, latitude, longitude)
            .map(result -> {
                if (result.isSuccess()) {
                    return result.getData();
                }
                return false;
            });
    }

    public float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        return loc1.distanceTo(loc2);
    }
}
