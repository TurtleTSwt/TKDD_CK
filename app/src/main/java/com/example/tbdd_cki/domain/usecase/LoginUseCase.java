package com.example.tbdd_cki.domain.usecase;

import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.model.User;
import com.example.tbdd_cki.domain.repository.UserRepository;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;

public class LoginUseCase {
    private final UserRepository userRepository;

    @Inject
    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Single<Result<User>> execute(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Username cannot be empty"),
                "Username cannot be empty"
            ));
        }

        if (password == null || password.trim().isEmpty()) {
            return Single.just(Result.error(
                new IllegalArgumentException("Password cannot be empty"),
                "Password cannot be empty"
            ));
        }

        return userRepository.login(username, password);
    }
}
