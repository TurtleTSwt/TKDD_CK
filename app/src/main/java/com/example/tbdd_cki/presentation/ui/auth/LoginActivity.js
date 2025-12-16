package com.example.tbdd_cki.presentation.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.app.R;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.model.UserRole;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.LoginUseCase;
import com.example.tbdd_cki.presentation.ui.student.StudentMainActivity;
import com.example.tbdd_cki.presentation.ui.teacher.TeacherMainActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private UserRepository userRepository;
    private LoginUseCase loginUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepositoryImpl(this);
        loginUseCase = new LoginUseCase(userRepository);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> goToRegister());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        disposables.add(
            loginUseCase.execute(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                        if (result.getData().getRole() == UserRole.STUDENT) {
                            startActivity(new Intent(this, StudentMainActivity.class));
                        } else {
                            startActivity(new Intent(this, TeacherMainActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
