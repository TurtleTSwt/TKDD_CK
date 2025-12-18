package com.example.tbdd_cki.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tbdd_cki.domain.model.ClassModel;
import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.repository.ClassRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ClassRepositoryImpl implements ClassRepository {
    private static final String BASE_URL = "URL";
    private static final String PREFS_NAME = "AttendanceAppPrefs";
    private SharedPreferences prefs;

    public ClassRepositoryImpl(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Single<Result<List<ClassModel>>> getTeacherClasses() {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes/teacher");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                BufferedReader reader;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    List<ClassModel> classes = parseClassList(new JSONObject(response.toString()));
                    return Result.success(classes);
                } else {
                    JSONObject errorObj = new JSONObject(response.toString());
                    String errorMessage = errorObj.optString("message", "Lỗi không xác định");
                    return Result.error(new Exception(errorMessage), errorMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    // Alternative method if interface requires teacherId parameter
    public Single<Result<List<ClassModel>>> getClassesByTeacher(String teacherId) {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes/teacher/" + teacherId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");

                int responseCode = conn.getResponseCode();
                BufferedReader reader;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    List<ClassModel> classes = parseClassList(new JSONObject(response.toString()));
                    return Result.success(classes);
                } else {
                    JSONObject errorObj = new JSONObject(response.toString());
                    String errorMessage = errorObj.optString("message", "Lỗi không xác định");
                    return Result.error(new Exception(errorMessage), errorMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    @Override
    public Single<Result<ClassModel>> createClass(ClassModel classModel) {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", classModel.getName());
                jsonBody.put("code", classModel.getCode());
                jsonBody.put("description", classModel.getDescription());

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    ClassModel createdClass = parseClass(new JSONObject(response.toString()));
                    return Result.success(createdClass);
                } else {
                    JSONObject errorObj = new JSONObject(response.toString());
                    String errorMessage = errorObj.optString("message", "Lỗi không xác định");
                    return Result.error(new Exception(errorMessage), errorMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    @Override
    public Single<Result<ClassModel>> updateClass(ClassModel classModel) {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes/" + classModel.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", classModel.getName());
                jsonBody.put("code", classModel.getCode());
                jsonBody.put("description", classModel.getDescription());

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode == HttpURLConnection.HTTP_OK
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    ClassModel updatedClass = parseClass(new JSONObject(response.toString()));
                    return Result.success(updatedClass);
                } else {
                    JSONObject errorObj = new JSONObject(response.toString());
                    String errorMessage = errorObj.optString("message", "Lỗi không xác định");
                    return Result.error(new Exception(errorMessage), errorMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    @Override
    public Single<Result<ClassModel>> getClassById(String classId) {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes/" + classId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                responseCode == HttpURLConnection.HTTP_OK
                                        ? conn.getInputStream()
                                        : conn.getErrorStream()
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    ClassModel classModel = parseClass(new JSONObject(response.toString()));
                    return Result.success(classModel);
                } else {
                    JSONObject errorObj = new JSONObject(response.toString());
                    String errorMessage = errorObj.optString("message", "Lỗi không xác định");
                    return Result.error(new Exception(errorMessage), errorMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    @Override
    public Single<Result<Void>> deleteClass(String classId) {
        return Single.fromCallable(() -> {
            try {
                String token = prefs.getString("auth_token", "");
                if (token.isEmpty()) {
                    return Result.error(new Exception("Token không hợp lệ"), "Token không hợp lệ");
                }

                URL url = new URL(BASE_URL + "/classes/" + classId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    return Result.success(null);
                } else {
                    return Result.error(new Exception("Không thể xóa lớp học"), "Không thể xóa lớp học");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e, "Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    private List<ClassModel> parseClassList(JSONObject response) throws Exception {
        List<ClassModel> classes = new ArrayList<>();
        JSONArray dataArray = response.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject classObj = dataArray.getJSONObject(i);
            classes.add(parseClass(classObj));
        }

        return classes;
    }

    private ClassModel parseClass(JSONObject json) throws Exception {
        ClassModel classModel = new ClassModel();

        // Parse dữ liệu theo cấu trúc API của bạn
        if (json.has("data")) {
            json = json.getJSONObject("data");
        }

        classModel.setId(json.optString("_id", ""));
        classModel.setName(json.optString("name", ""));
        classModel.setCode(json.optString("code", ""));
        classModel.setDescription(json.optString("description", ""));
        classModel.setActive(json.optBoolean("isActive", true));
        classModel.setStudentCount(json.optInt("studentCount", 0));

        return classModel;
    }
}