package com.example.app_learn_chinese_2025.model.response;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("code")
    private Integer code;

    // Constructor
    public ApiResponse() {}

    public ApiResponse(Boolean success, String message, T data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // Getters and Setters
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    // Utility methods
    public boolean isSuccessful() {
        return success != null && success;
    }

    public boolean hasData() {
        return data != null;
    }

    public String getErrorMessage() {
        if (isSuccessful()) return null;
        return message != null ? message : "Có lỗi xảy ra";
    }
}