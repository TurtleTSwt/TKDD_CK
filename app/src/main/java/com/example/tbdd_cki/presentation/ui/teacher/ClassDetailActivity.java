package com.example.tbdd_cki.presentation.ui.teacher;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.ClassRepositoryImpl;
import com.example.tbdd_cki.domain.model.ClassModel;
import com.example.tbdd_cki.domain.repository.ClassRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClassDetailActivity extends AppCompatActivity {
    private TextView tvClassName;
    private TextView tvClassCode;
    private TextView tvClassDescription;
    private TextView tvStudentCount;
    private TextView tvClassStatus;
    private ProgressBar progressBar;
    private Button btnEditClass;
    private Button btnDeleteClass;
    private Button btnViewStudents;
    private Toolbar toolbar;

    private ClassRepository classRepository;
    private CompositeDisposable disposables = new CompositeDisposable();
    private String classId;
    private ClassModel currentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        classId = getIntent().getStringExtra("CLASS_ID");
        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin lớp học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        classRepository = new ClassRepositoryImpl(this);
        initViews();
        setupToolbar();
        loadClassDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvClassName = findViewById(R.id.tvClassName);
        tvClassCode = findViewById(R.id.tvClassCode);
        tvClassDescription = findViewById(R.id.tvClassDescription);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvClassStatus = findViewById(R.id.tvClassStatus);
        progressBar = findViewById(R.id.progressBar);
        btnEditClass = findViewById(R.id.btnEditClass);
        btnDeleteClass = findViewById(R.id.btnDeleteClass);
        btnViewStudents = findViewById(R.id.btnViewStudents);

        btnEditClass.setOnClickListener(v -> editClass());
        btnDeleteClass.setOnClickListener(v -> confirmDeleteClass());
        btnViewStudents.setOnClickListener(v -> viewStudents());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết lớp học");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadClassDetails() {
        showLoading(true);

        disposables.add(
                classRepository.getClassById(classId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            showLoading(false);
                            if (result.isSuccess()) {
                                currentClass = result.getData();
                                displayClassDetails(currentClass);
                            } else {
                                showError(result.getMessage());
                            }
                        }, error -> {
                            showLoading(false);
                            showError("Lỗi kết nối: " + error.getMessage());
                        })
        );
    }

    private void displayClassDetails(ClassModel classModel) {
        tvClassName.setText(classModel.getName());
        tvClassCode.setText("Mã lớp: " + classModel.getCode());
        tvClassDescription.setText(classModel.getDescription());
        tvStudentCount.setText("Số sinh viên: " + classModel.getStudentCount());
        tvClassStatus.setText(classModel.isActive() ? "Trạng thái: Đang hoạt động" : "Trạng thái: Không hoạt động");
    }

    private void editClass() {
        // TODO: Implement edit class functionality
        Toast.makeText(this, "Chức năng chỉnh sửa đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private void confirmDeleteClass() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp học này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteClass())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteClass() {
        showLoading(true);

        disposables.add(
                classRepository.deleteClass(classId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            showLoading(false);
                            if (result.isSuccess()) {
                                Toast.makeText(this, "Xóa lớp học thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                showError(result.getMessage());
                            }
                        }, error -> {
                            showLoading(false);
                            showError("Lỗi kết nối: " + error.getMessage());
                        })
        );
    }

    private void viewStudents() {
        Toast.makeText(this, "Chức năng xem danh sách sinh viên đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnEditClass.setEnabled(!show);
        btnDeleteClass.setEnabled(!show);
        btnViewStudents.setEnabled(!show);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}