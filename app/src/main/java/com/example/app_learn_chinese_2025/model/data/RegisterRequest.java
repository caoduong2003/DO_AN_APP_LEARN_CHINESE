package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RegisterRequest implements Serializable {
    @SerializedName("tenDangNhap")
    private String tenDangNhap;

    @SerializedName("email")
    private String email;

    @SerializedName("matKhau")
    private String matKhau;

    @SerializedName("hoTen")
    private String hoTen;

    @SerializedName("soDienThoai")
    private String soDienThoai;

    @SerializedName("vaiTro")
    private Integer vaiTro;

    @SerializedName("trinhDoHSK")
    private Integer trinhDoHSK;

    // Constructor mặc định
    public RegisterRequest() {
        this.vaiTro = 2; // Mặc định là học sinh
        this.trinhDoHSK = 0; // Mặc định là cấp độ 0
    }

    // Constructor cho đăng nhập
    public RegisterRequest(String tenDangNhap, String matKhau) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = 2;
        this.trinhDoHSK = 0;
    }

    // Constructor cho đăng ký
    public RegisterRequest(String tenDangNhap, String email, String matKhau, String hoTen, String soDienThoai) {
        this.tenDangNhap = tenDangNhap;
        this.email = email;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.vaiTro = 2; // Mặc định là học sinh
        this.trinhDoHSK = 0; // Mặc định là cấp độ 0
    }

    // Constructor đầy đủ
    public RegisterRequest(String tenDangNhap, String email, String matKhau, String hoTen, String soDienThoai, Integer vaiTro, Integer trinhDoHSK) {
        this.tenDangNhap = tenDangNhap;
        this.email = email;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.trinhDoHSK = trinhDoHSK;
    }

    // Getters và setters
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Integer getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(Integer vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Integer getTrinhDoHSK() {
        return trinhDoHSK;
    }

    public void setTrinhDoHSK(Integer trinhDoHSK) {
        this.trinhDoHSK = trinhDoHSK;
    }
}