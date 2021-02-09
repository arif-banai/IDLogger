package me.arifbanai.idLogger.interfaces;

public interface IDLoggerCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception cause);
}
