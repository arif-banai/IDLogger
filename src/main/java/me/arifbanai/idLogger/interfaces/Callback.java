package me.arifbanai.idLogger.interfaces;

public interface Callback<T> {
    public void onSuccess(T result);
    public void onFailure(Throwable cause);
}