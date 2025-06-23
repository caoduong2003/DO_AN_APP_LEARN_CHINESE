package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private String token;
    private String type;
    private long id;
    private String tenDangNhap;
    private String email;
    private int vaiTro;
    private String hoTen;

    // Constructor mặc định
    public JwtResponse() {}

    // Constructor đầy đủ
    public JwtResponse(String token, String type, long id, String tenDangNhap, String email, int vaiTro, String hoTen) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.email = email;
        this.vaiTro = vaiTro;
        this.hoTen = hoTen;
    }

    // Getters và setters
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

    // Tạo User từ các trường trong JwtResponse
    public User getUser() {
        User user = new User();
        user.setID(this.id);
        user.setTenDangNhap(this.tenDangNhap);
        user.setEmail(this.email);
        user.setVaiTro(this.vaiTro);
        user.setHoTen(this.hoTen);
        user.setTrangThai(true); // Gán mặc định vì response không có trangThai
        return user;
    }
}