package com.example.tbdd_cki.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.tbdd_cki.data.source.local.database.AppDatabase;
import com.example.tbdd_cki.data.source.local.database.entity.UserEntity;
import com.example.tbdd_cki.domain.model.*;
import com.example.tbdd_cki.domain.repository.UserRepository;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Date;

public class UserRepositoryImpl implements UserRepository {
    private final AppDatabase database;
    private final SharedPreferences prefs;
    private static final String PREF_NAME = "attendance_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public UserRepositoryImpl(Context context) {
        this.database = AppDatabase.getInstance(context);
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Single<Result<User>> login(String username, String password) {
        return Single.fromCallable(() -> {
            UserEntity userEntity = database.userDao().login(username, password);
            if (userEntity == null) {
                return Result.<User>error(
                    new Exception("Invalid credentials"),
                    "Username or password is incorrect"
                );
            }

            prefs.edit()
                .putLong(KEY_USER_ID, userEntity.id)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();

            User user = mapToUser(userEntity);
            return Result.success(user);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<User>> register(String username, String password, 
                                        String fullName, String email, String role) {
        return Single.fromCallable(() -> {
            UserEntity existing = database.userDao().findByUsername(username);
            if (existing != null) {
                return Result.<User>error(
                    new Exception("Username already exists"),
                    "Username already exists"
                );
            }

            UserEntity userEntity = new UserEntity();
            userEntity.username = username;
            userEntity.password = password;
            userEntity.fullName = fullName;
            userEntity.email = email;
            userEntity.role = role;
            userEntity.createdAt = System.currentTimeMillis();

            if ("STUDENT".equals(role)) {
                userEntity.studentId = "SV" + System.currentTimeMillis();
            }

            long id = database.userDao().insert(userEntity);
            userEntity.id = id;

            User user = mapToUser(userEntity);
            return Result.success(user);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<User>> getCurrentUser() {
        return Single.fromCallable(() -> {
            long userId = prefs.getLong(KEY_USER_ID, -1);
            if (userId == -1) {
                return Result.<User>error(
                    new Exception("No user logged in"),
                    "No user logged in"
                );
            }

            UserEntity userEntity = database.userDao().getUserById(userId);
            if (userEntity == null) {
                return Result.<User>error(
                    new Exception("User not found"),
                    "User not found"
                );
            }

            User user = mapToUser(userEntity);
            return Result.success(user);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<User>> updateProfile(User user) {
        return Single.fromCallable(() -> {
            UserEntity entity = new UserEntity();
            entity.id = Long.parseLong(user.getId());
            entity.username = user.getUsername();
            entity.fullName = user.getFullName();
            entity.email = user.getEmail();
            entity.phone = user.getPhone();
            entity.role = user.getRole().name();

            database.userDao().update(entity);
            return Result.success(user);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Result<Void>> logout() {
        return Single.fromCallable(() -> {
            prefs.edit()
                .remove(KEY_USER_ID)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
            return Result.<Void>success(null);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> isLoggedIn() {
        return Observable.fromCallable(() ->
            prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        );
    }

    private User mapToUser(UserEntity entity) {
        return new User(
            String.valueOf(entity.id),
            entity.username,
            entity.fullName,
            entity.email,
            entity.phone,
            UserRole.valueOf(entity.role),
            entity.studentId,
            null,
            new Date(entity.createdAt),
            new Date(entity.createdAt)
        );
    }
}
