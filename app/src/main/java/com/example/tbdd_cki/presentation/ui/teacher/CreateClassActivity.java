package com.example.tbdd_cki.presentation.ui.teacher;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.source.local.database.AppDatabase ;
import com.example.tbdd_cki.data.source.local.database.entity.ClassEntity ;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateClassActivity extends AppCompatActivity {
    private EditText etClassCode, etClassName, etSubjectName, etSemester, etYear;
    private EditText etLatitude, etLongitude, etRadius;
    private Button btnCreateClass;
    private AppDatabase database;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Button btnUseCurrentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnUseCurrentLocation = findViewById(R.id.btnUseCurrentLocation);
        btnUseCurrentLocation.setOnClickListener(v -> useCurrentLocation());

        database = AppDatabase.getInstance(this);


        initViews();
    }

    private void initViews() {
        etClassCode = findViewById(R.id.etClassCode);
        etClassName = findViewById(R.id.etClassName);
        etSubjectName = findViewById(R.id.etSubjectName);
        etSemester = findViewById(R.id.etSemester);
        etYear = findViewById(R.id.etYear);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etRadius = findViewById(R.id.etRadius);
        btnCreateClass = findViewById(R.id.btnCreateClass);

        btnCreateClass.setOnClickListener(v -> createClass());
    }

    private void createClass() {
        String classCode = etClassCode.getText().toString().trim();
        String className = etClassName.getText().toString().trim();
        String subjectName = etSubjectName.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String latStr = etLatitude.getText().toString().trim();
        String lonStr = etLongitude.getText().toString().trim();
        String radiusStr = etRadius.getText().toString().trim();

        if (classCode.isEmpty() || className.isEmpty() || subjectName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ClassEntity classEntity = new ClassEntity();
        classEntity.classCode = classCode;
        classEntity.className = className;
        classEntity.subjectName = subjectName;
        classEntity.semester = semester;
        classEntity.academicYear = year;
        classEntity.teacherId = 1; // Demo teacher ID
        classEntity.teacherName = "Demo Teacher";
        classEntity.createdAt = System.currentTimeMillis();

        if (!latStr.isEmpty() && !lonStr.isEmpty() && !radiusStr.isEmpty()) {
            classEntity.latitude = Double.parseDouble(latStr);
            classEntity.longitude = Double.parseDouble(lonStr);
            classEntity.allowedRadius = Integer.parseInt(radiusStr);
        }

        disposables.add(
                Single.fromCallable(() -> {
                            database.classDao().insert(classEntity);
                            return true;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            Toast.makeText(this, "Class created successfully!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        })
        );
    }
    private void useCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 103);
            return;
        }

        Toast.makeText(this, "Getting your location...", Toast.LENGTH_SHORT).show();

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            etLatitude.setText(String.valueOf(location.getLatitude()));
                            etLongitude.setText(String.valueOf(location.getLongitude()));
                            Toast.makeText(CreateClassActivity.this,
                                    "Location set successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateClassActivity.this,
                                    "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}