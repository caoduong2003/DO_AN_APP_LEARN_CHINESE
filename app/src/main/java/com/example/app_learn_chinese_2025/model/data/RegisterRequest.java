package com.example.app_learn_chinese_2025.model.data;


public class RegisterRequest {
    private String TenDangNhap;
    private String Email;
    private String MatKhau;
    private String HoTen;
    private String SoDienThoai;
    private int VaiTro; // Mặc định 2 - học sinh
    private int TrinhDoHSK; // Mặc định 0

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

    // Getters và Setters
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