package com.example.tbdd_cki.domain.repository;

import com.example.tbdd_cki.domain.model.ClassModel;
import com.example.tbdd_cki.domain.model.Result;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface ClassRepository {
    Single<Result<List<ClassModel>>> getTeacherClasses();

    Single<Result<List<ClassModel>>> getClassesByTeacher(String teacherId);

    Single<Result<ClassModel>> createClass(ClassModel classModel);

    Single<Result<ClassModel>> updateClass(ClassModel classModel);

    Single<Result<ClassModel>> getClassById(String classId);

    Single<Result<Void>> deleteClass(String classId);
}