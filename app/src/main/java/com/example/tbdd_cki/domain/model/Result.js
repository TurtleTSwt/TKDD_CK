package com.example.tbdd_cki.domain.model;

public class Result<T> {
    private T data;
    private Exception exception;
    private String message;
    private Status status;

    public enum Status {
        SUCCESS, ERROR, LOADING
    }

    private Result(Status status, T data, Exception exception, String message) {
        this.status = status;
        this.data = data;
        this.exception = exception;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Result<T> error(Exception exception, String message) {
        return new Result<>(Status.ERROR, null, exception, message);
    }

    public static <T> Result<T> loading() {
        return new Result<>(Status.LOADING, null, null, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    // Getters
    public T getData() { return data; }
    public Exception getException() { return exception; }
    public String getMessage() { return message; }
    public Status getStatus() { return status; }
}