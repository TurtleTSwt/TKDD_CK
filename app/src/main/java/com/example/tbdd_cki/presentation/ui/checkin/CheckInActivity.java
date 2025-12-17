package com.example.tbdd_cki.presentation.ui.checkin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.model.*;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.CheckInUseCase;
import com.example.tbdd_cki.domain.usecase.LocationValidator;
import com.google.android.gms.location.*;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class CheckInActivity extends AppCompatActivity {
    private TextView tvClassName, tvSessionTime, tvLocation;
    private Button btnCheckIn;
    private FusedLocationProviderClient fusedLocationClient;
    private UserRepository userRepository;
    private AttendanceRepository attendanceRepository;
    private CheckInUseCase checkInUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();
    private String sessionId;
    private String currentUserId;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        sessionId = getIntent().getStringExtra("SESSION_ID");

        tvClassName = findViewById(R.id.tvClassName);
        tvSessionTime = findViewById(R.id.tvSessionTime);
        tvLocation = findViewById(R.id.tvLocation);
        btnCheckIn = findViewById(R.id.btnCheckIn);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        userRepository = new UserRepositoryImpl(this);
        attendanceRepository = new AttendanceRepositoryImpl(this);
        LocationValidator locationValidator = new LocationValidator(attendanceRepository);
        checkInUseCase = new CheckInUseCase(attendanceRepository, locationValidator);

        loadCurrentUser();
        loadSessionDetails();
        getCurrentLocation();

        btnCheckIn.setOnClickListener(v -> checkIn());
    }

    private void loadCurrentUser() {
        disposables.add(
                userRepository.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                currentUserId = result.getData().getId();
                            }
                        }, error -> {})
        );
    }

    private void loadSessionDetails() {
        disposables.add(
                attendanceRepository.getSessionById(sessionId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                AttendanceSession session = result.getData();
                                tvClassName.setText(session.getClassName());
                                tvSessionTime.setText(session.getSessionTime());
                            }
                        }, error -> {})
        );
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
            return;
        }

        // Show loading
        tvLocation.setText("Getting your location...");

        // Request fresh location
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            currentLatitude = locationResult.getLastLocation().getLatitude();
                            currentLongitude = locationResult.getLastLocation().getLongitude();
                            tvLocation.setText(String.format("Your Location:\nLat: %.6f\nLon: %.6f",
                                    currentLatitude, currentLongitude));
                            btnCheckIn.setEnabled(true);
                        } else {
                            tvLocation.setText("Unable to get location");
                            Toast.makeText(CheckInActivity.this,
                                    "Please enable GPS and try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, null);
    }

    private void checkIn() {
        CheckInRequest request = new CheckInRequest(
                sessionId,
                currentUserId,
                null,
                currentLatitude,
                currentLongitude,
                AttendanceMethod.GPS
        );

        disposables.add(
                checkInUseCase.execute(request)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                Toast.makeText(this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}