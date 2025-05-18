package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
public class LoginRequest {
    // Sử dụng @SerializedName để đảm bảo đúng tên field
    @SerializedName("tenDangNhap")
    private String TenDangNhap;

    @SerializedName("matKhau")
    private String MatKhau;

    public LoginRequest(String tenDangNhap, String matKhau) {
        this.TenDangNhap = tenDangNhap;
        this.MatKhau = matKhau;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.TenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        this.MatKhau = matKhau;
    }
}