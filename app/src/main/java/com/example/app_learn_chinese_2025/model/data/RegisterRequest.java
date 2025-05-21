package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    // Sử dụng @SerializedName để đảm bảo đúng tên field khi gửi JSON
    @SerializedName("tenDangNhap")
    private String TenDangNhap;

    @SerializedName("email")
    private String Email;

    @SerializedName("matKhau")
    private String MatKhau;

    @SerializedName("hoTen")
    private String HoTen;

    @SerializedName("soDienThoai")
    private String SoDienThoai;

    @SerializedName("vaiTro")
    private int VaiTro;

    @SerializedName("trinhDoHSK")
    private int TrinhDoHSK;

    public RegisterRequest() {
        this.VaiTro = 2; // Mặc định là học sinh
        this.TrinhDoHSK = 0; // Mặc định là cấp độ 0
    }

    public RegisterRequest(String tenDangNhap, String email, String matKhau, String hoTen, String soDienThoai) {
        this.TenDangNhap = tenDangNhap;
        this.Email = email;
        this.MatKhau = matKhau;
        this.HoTen = hoTen;
        this.SoDienThoai = soDienThoai;
        this.VaiTro = 2; // Mặc định là học sinh
        this.TrinhDoHSK = 0; // Mặc định là cấp độ 0
    }

    // Các getters và setters
    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.TenDangNhap = tenDangNhap;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        this.MatKhau = matKhau;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        this.HoTen = hoTen;
    }

    public String getSoDienThoai() {
        return SoDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.SoDienThoai = soDienThoai;
    }

    public int getVaiTro() {
        return VaiTro;
    }

    public void setVaiTro(int vaiTro) {
        this.VaiTro = vaiTro;
    }

    public int getTrinhDoHSK() {
        return TrinhDoHSK;
    }

    public void setTrinhDoHSK(int trinhDoHSK) {
        this.TrinhDoHSK = trinhDoHSK;
    }
}