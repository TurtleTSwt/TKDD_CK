package com.example.tbdd_cki.presentation.ui.history;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.model.AttendanceRecord;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.GetAttendanceHistoryUseCase;
import com.example.tbdd_cki.presentation.adapter.AttendanceHistoryAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.List;

public class AttendanceHistoryActivity extends AppCompatActivity {
    private TextView tvStats;
    private RecyclerView rvHistory;
    private UserRepository userRepository;
    private AttendanceRepository attendanceRepository;
    private GetAttendanceHistoryUseCase getHistoryUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();
    private AttendanceHistoryAdapter adapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        userRepository = new UserRepositoryImpl(this);
        attendanceRepository = new AttendanceRepositoryImpl(this);
        getHistoryUseCase = new GetAttendanceHistoryUseCase(attendanceRepository);

        initViews();
        loadCurrentUser();
    }

    private void initViews() {
        tvStats = findViewById(R.id.tvStats);
        rvHistory = findViewById(R.id.rvHistory);

        adapter = new AttendanceHistoryAdapter(new ArrayList<>());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void loadCurrentUser() {
        disposables.add(
                userRepository.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                currentUserId = result.getData().getId();
                                loadHistory();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void loadHistory() {
        disposables.add(
                getHistoryUseCase.execute(currentUserId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(records -> {
                            adapter.updateData(records);
                            updateStats(records);
                        }, error -> {
                            Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void updateStats(List<AttendanceRecord> records) {
        int total = records.size();
        long present = records.stream()
                .filter(r -> "PRESENT".equals(r.getStatus().name()))
                .count();

        String stats = String.format("Total: %d | Present: %d | Attendance Rate: %.1f%%",
                total, present, total > 0 ? (present * 100f / total) : 0);
        tvStats.setText(stats);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}





