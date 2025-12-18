package com.example.tbdd_cki.presentation.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.data.repository.ClassRepositoryImpl;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.repository.ClassRepository;
import com.example.tbdd_cki.domain.model.ClassModel;
import com.example.tbdd_cki.presentation.adapter.ClassAdapter;
import com.example.tbdd_cki.presentation.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TeacherMainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private TextView tvEmptyState;
    private RecyclerView rvClasses;
    private ProgressBar progressBar;
    private Button btnCreateClass, btnCreateSession, btnLogout;

    private UserRepository userRepository;
    private ClassRepository classRepository;
    private CompositeDisposable disposables = new CompositeDisposable();
    private ClassAdapter classAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        userRepository = new UserRepositoryImpl(this);
        classRepository = new ClassRepositoryImpl(this); // Cần tạo repository này

        initViews();
        setupRecyclerView();
        loadCurrentUser();
        loadTeacherClasses(); // Load danh sách lớp
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmptyState = findViewById(R.id.tvEmptyState); // Thêm trong layout
        rvClasses = findViewById(R.id.rvClasses);
        progressBar = findViewById(R.id.progressBar); // Thêm trong layout
        btnCreateClass = findViewById(R.id.btnCreateClass);
        btnCreateSession = findViewById(R.id.btnCreateSession);
        btnLogout = findViewById(R.id.btnLogout);

        btnCreateClass.setOnClickListener(v -> createClass());
        btnCreateSession.setOnClickListener(v -> createSession());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupRecyclerView() {
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        classAdapter = new ClassAdapter(new ArrayList<>(), this::onClassClick);
        rvClasses.setAdapter(classAdapter);
    }

    private void loadCurrentUser() {
        disposables.add(
                userRepository.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                tvWelcome.setText("Xin chào, " + result.getData().getFullName());
                            }
                        }, error -> {
                            Toast.makeText(this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void loadTeacherClasses() {
        showLoading(true);

        disposables.add(
                classRepository.getTeacherClasses() // API lấy lớp của giáo viên
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            showLoading(false);
                            if (result.isSuccess()) {
                                List<ClassModel> classes = result.getData();
                                if (classes != null && !classes.isEmpty()) {
                                    showClasses(classes);
                                } else {
                                    showEmptyState();
                                }
                            } else {
                                // Hiển thị message từ Result
                                String errorMsg = result.getMessage();
                                if (errorMsg == null || errorMsg.isEmpty()) {
                                    errorMsg = "Không thể tải danh sách lớp";
                                }
                                showError(errorMsg);
                            }
                        }, error -> {
                            showLoading(false);
                            showError("Lỗi kết nối: " + error.getMessage());
                        })
        );
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        rvClasses.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showClasses(List<ClassModel> classes) {
        tvEmptyState.setVisibility(View.GONE);
        rvClasses.setVisibility(View.VISIBLE);
        classAdapter.updateClasses(classes);
    }

    private void showEmptyState() {
        rvClasses.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("Bạn chưa có lớp học nào.\nNhấn 'Tạo lớp học' để bắt đầu.");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onClassClick(ClassModel classModel) {
        // Xử lý khi click vào lớp học
        Intent intent = new Intent(this, ClassDetailActivity.class);
        intent.putExtra("CLASS_ID", classModel.getId());
        startActivity(intent);
    }

    private void createClass() {
        startActivity(new Intent(this, CreateClassActivity.class));
    }

    private void createSession() {
        startActivity(new Intent(this, CreateSessionActivity.class));
    }

    private void logout() {
        disposables.add(
                userRepository.logout()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        }, error -> {
                            Toast.makeText(this, "Lỗi đăng xuất", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeacherClasses();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}