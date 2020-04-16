package me.arifbanai.idLogger.interfaces;

public interface IDLoggerCallback<T> {
    public void onSuccess(T result);
    public void onFailure(Throwable cause);
}
