package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.model.User;
import com.example.tbdd_cki.domain.repository.UserRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class RegisterUseCase {
    private final UserRepository userRepository;

    @Inject
    public RegisterUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<Result<User>> execute(String username, String password, 
                                        String fullName, String email, String role) {
        if (username == null || username.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Username cannot be empty"),
                "Username cannot be empty"
            ));
        }

        if (password == null || password.length() < 6) {
            return Single.just(Result.error(
                new IllegalArgumentException("Password must be at least 6 characters"),
                "Password must be at least 6 characters"
            ));
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Full name cannot be empty"),
                "Full name cannot be empty"
            ));
        }

        if (!isValidEmail(email)) {
            return Single.just(Result.error(
                new IllegalArgumentException("Invalid email format"),
                "Invalid email format"
            ));
        }

        return userRepository.register(username, password, fullName, email, role);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
