// presentation/ui/TeacherMainActivity.java
package com.example.tbdd_cki.presentation.ui.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attendance.app.R;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.repository.UserRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TeacherMainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private RecyclerView rvClasses;
    private Button btnCreateClass, btnCreateSession, btnLogout;
    private UserRepository userRepository;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        userRepository = new UserRepositoryImpl(this);

        initViews();
        loadCurrentUser();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        rvClasses = findViewById(R.id.rvClasses);
        btnCreateClass = findViewById(R.id.btnCreateClass);
        btnCreateSession = findViewById(R.id.btnCreateSession);
        btnLogout = findViewById(R.id.btnLogout);

        rvClasses.setLayoutManager(new LinearLayoutManager(this));

        btnCreateClass.setOnClickListener(v -> createClass());
        btnCreateSession.setOnClickListener(v -> createSession());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadCurrentUser() {
        disposables.add(
                userRepository.getCurrentUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                tvWelcome.setText("Welcome, " + result.getData().getFullName());
                            }
                        }, error -> {
                            Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show();
                        })
        );
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
                            Toast.makeText(this, "Error logging out", Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}



