package com.example.tbdd_cki.presentation.ui.student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.model.AttendanceSession;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.GetActiveSessionsUseCase;
import com.example.tbdd_cki.presentation.ui.history.AttendanceHistoryActivity;
import com.example.tbdd_cki.presentation.adapter.SessionAdapter;
import com.example.tbdd_cki.presentation.ui.auth.LoginActivity;
import com.example.tbdd_cki.presentation.ui.checkin.CheckInActivity;
import com.example.tbdd_cki.presentation.ui.scanner.QRScannerActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.List;

public class StudentMainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private RecyclerView rvActiveSessions;
    private Button btnScanQR, btnHistory, btnLogout;
    private UserRepository userRepository;
    private AttendanceRepository attendanceRepository;
    private GetActiveSessionsUseCase getActiveSessionsUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();
    private SessionAdapter sessionAdapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        userRepository = new UserRepositoryImpl(this);
        attendanceRepository = new AttendanceRepositoryImpl(this);
        getActiveSessionsUseCase = new GetActiveSessionsUseCase(attendanceRepository);

        initViews();
        loadCurrentUser();
        setupRecyclerView();
        requestPermissions();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        rvActiveSessions = findViewById(R.id.rvActiveSessions);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnHistory = findViewById(R.id.btnHistory);
        btnLogout = findViewById(R.id.btnLogout);

        btnScanQR.setOnClickListener(v -> openQRScanner());
        btnHistory.setOnClickListener(v -> openHistory());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupRecyclerView() {
        sessionAdapter = new SessionAdapter(new ArrayList<>(), this::onSessionClick);
        rvActiveSessions.setLayoutManager(new LinearLayoutManager(this));
        rvActiveSessions.setAdapter(sessionAdapter);
    }

    private void loadCurrentUser() {
        disposables.add(
            userRepository.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        currentUserId = result.getData().getId();
                        tvWelcome.setText("Welcome, " + result.getData().getFullName());
                        loadActiveSessions();
                    }
                }, error -> {
                    Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void loadActiveSessions() {
        disposables.add(
            getActiveSessionsUseCase.execute(currentUserId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sessions -> {
                    sessionAdapter.updateData(sessions);
                }, error -> {
                    Toast.makeText(this, "Error loading sessions", Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void onSessionClick(AttendanceSession session) {
        Intent intent = new Intent(this, CheckInActivity.class);
        intent.putExtra("SESSION_ID", session.getId());
        startActivity(intent);
    }

    private void openQRScanner() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivity(intent);
    }

    private void openHistory() {
        Intent intent = new Intent(this, AttendanceHistoryActivity.class);
        startActivity(intent);
    }

    private void logout() {
        disposables.add(
            userRepository.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }, error -> {
                    Toast.makeText(this, "Error logging out", Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
