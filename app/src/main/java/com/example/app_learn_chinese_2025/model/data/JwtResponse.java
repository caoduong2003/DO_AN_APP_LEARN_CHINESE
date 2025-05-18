package com.example.app_learn_chinese_2025.model.data;

public class JwtResponse {
    private String token;
    private String type;
    private long id;
    private String tenDangNhap;
    private String email;
    private int vaiTro;
    private String hoTen;

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getVaiTro() { return vaiTro; }
    public void setVaiTro(int vaiTro) { this.vaiTro = vaiTro; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
}