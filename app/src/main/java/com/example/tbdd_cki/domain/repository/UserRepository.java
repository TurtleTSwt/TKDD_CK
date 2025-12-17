package com.example.tbdd_cki.domain.repository;

import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.model.User;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Single<Result<User>> login(String username, String password);
    Single<Result<User>> register(String username, String password, String fullName, String email, String role);
    Single<Result<User>> getCurrentUser();
    Single<Result<User>> updateProfile(User user);
    Single<Result<Void>> logout();
    Observable<Boolean> isLoggedIn();
}
