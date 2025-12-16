package com.example.tbdd_cki.domain.repository;

import com.example.tbdd_cki.domain.model.ClassModel;
import com.example.tbdd_cki.domain.model.Result;
import com.example.tbdd_cki.domain.model.User;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

public interface ClassRepository {
    Single<Result<ClassModel>> createClass(ClassModel classModel);
    Single<Result<ClassModel>> updateClass(ClassModel classModel);
    Single<Result<Void>> deleteClass(String classId);
    Single<Result<ClassModel>> getClassById(String classId);
    Observable<List<ClassModel>> getClassesByTeacher(String teacherId);
    Observable<List<ClassModel>> getClassesByStudent(String studentId);
    Single<Result<Void>> enrollStudent(String classId, String studentId);
    Single<Result<Void>> removeStudent(String classId, String studentId);
    Observable<List<User>> getStudentsInClass(String classId);
}
