// presentation/ui/RegisterActivity.java
package com.example.tbdd_cki.presentation.ui.auth;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.app.R;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.RegisterUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etFullName, etEmail;
    private RadioGroup rgRole;
    private Button btnRegister;
    private UserRepository userRepository;
    private RegisterUseCase registerUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepositoryImpl(this);
        registerUseCase = new RegisterUseCase(userRepository);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        int selectedId = rgRole.getCheckedRadioButtonId();
        String role = selectedId == R.id.rbStudent ? "STUDENT" : "TEACHER";

        disposables.add(
            registerUseCase.execute(username, password, fullName, email, role)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
