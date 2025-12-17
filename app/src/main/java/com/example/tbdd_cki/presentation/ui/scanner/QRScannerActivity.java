package com.example.tbdd_cki.presentation.ui.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.model.*;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.CheckInUseCase;
import com.example.tbdd_cki.domain.usecase.LocationValidator;
import com.google.android.gms.location.*;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;

public class QRScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private DecoratedBarcodeView barcodeView;
    private FusedLocationProviderClient fusedLocationClient;
    private UserRepository userRepository;
    private AttendanceRepository attendanceRepository;
    private CheckInUseCase checkInUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();
    private String currentUserId;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeView = findViewById(R.id.barcodeView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userRepository = new UserRepositoryImpl(this);
        attendanceRepository = new AttendanceRepositoryImpl(this);
        LocationValidator locationValidator = new LocationValidator(attendanceRepository);
        checkInUseCase = new CheckInUseCase(attendanceRepository, locationValidator);

        loadCurrentUser();
        getCurrentLocation();

        if (checkPermissions()) {
            startScanning();
        } else {
            requestPermissions();
        }
    }

    private void loadCurrentUser() {
        disposables.add(
                userRepository.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                currentUserId = result.getData().getId();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        // Request fresh location instead of last location
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
                            Toast.makeText(QRScannerActivity.this,
                                    "Location: " + currentLatitude + ", " + currentLongitude,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private void startScanning() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null) {
                    handleQRCodeScanned(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    private void handleQRCodeScanned(String qrCode) {
        barcodeView.pause();

        // Parse QR code to get session ID
        // Format: "QR_sessionId"
        String sessionId = extractSessionId(qrCode);

        if (sessionId == null) {
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CheckInRequest request = new CheckInRequest(
                sessionId,
                currentUserId,
                qrCode,
                currentLatitude,
                currentLongitude,
                AttendanceMethod.QR_CODE
        );

        disposables.add(
                checkInUseCase.execute(request)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                Toast.makeText(this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        })
        );
    }

    private String extractSessionId(String qrCode) {
        // Simple extraction: QR_<UUID>
        // In real app, you might want to decode a more complex format
        if (qrCode.startsWith("QR_")) {
            return qrCode.substring(3);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}