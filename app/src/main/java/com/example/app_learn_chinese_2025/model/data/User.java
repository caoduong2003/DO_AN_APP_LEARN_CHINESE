package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class User implements Serializable {
    private long ID; // Đổi sang long
    private String TenDangNhap;
    private String Email;
    private String MatKhau;
    private String HoTen;
    private String SoDienThoai;
    private int VaiTro;
    private int TrinhDoHSK;
    private String HinhDaiDien;
    private String NgayTao;
    private String NgayCapNhat;
    private String LanDangNhapCuoi;
    private boolean TrangThai; // Đổi sang boolean

    // Constructor mặc định
    public User() {}

    // Constructor đầy đủ
    public User(long ID, String tenDangNhap, String email, String matKhau, String hoTen,
                String soDienThoai, int vaiTro, int trinhDoHSK, String hinhDaiDien,
                String ngayTao, String ngayCapNhat, String lanDangNhapCuoi, boolean trangThai) {
        this.ID = ID;
        this.TenDangNhap = tenDangNhap;
        this.Email = email;
        this.MatKhau = matKhau;
        this.HoTen = hoTen;
        this.SoDienThoai = soDienThoai;
        this.VaiTro = vaiTro;
        this.TrinhDoHSK = trinhDoHSK;
        this.HinhDaiDien = hinhDaiDien;
        this.NgayTao = ngayTao;
        this.NgayCapNhat = ngayCapNhat;
        this.LanDangNhapCuoi = lanDangNhapCuoi;
        this.TrangThai = trangThai;
    }

    // Getters và setters
    public long getID() { return ID; }
    public void setID(long ID) { this.ID = ID; }
    public String getTenDangNhap() { return TenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.TenDangNhap = tenDangNhap; }
    public String getEmail() { return Email; }
    public void setEmail(String email) { this.Email = email; }
    public String getMatKhau() { return MatKhau; }
    public void setMatKhau(String matKhau) { this.MatKhau = matKhau; }
    public String getHoTen() { return HoTen; }
    public void setHoTen(String hoTen) { this.HoTen = hoTen; }
    public String getSoDienThoai() { return SoDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.SoDienThoai = soDienThoai; }
    public int getVaiTro() { return VaiTro; }
    public void setVaiTro(int vaiTro) { this.VaiTro = vaiTro; }
    public int getTrinhDoHSK() { return TrinhDoHSK; }
    public void setTrinhDoHSK(int trinhDoHSK) { this.TrinhDoHSK = trinhDoHSK; }
    public String getHinhDaiDien() { return HinhDaiDien; }
    public void setHinhDaiDien(String hinhDaiDien) { this.HinhDaiDien = hinhDaiDien; }
    public String getNgayTao() { return NgayTao; }
    public void setNgayTao(String ngayTao) { this.NgayTao = ngayTao; }
    public String getNgayCapNhat() { return NgayCapNhat; }
    public void setNgayCapNhat(String ngayCapNhat) { this.NgayCapNhat = ngayCapNhat; }
    public String getLanDangNhapCuoi() { return LanDangNhapCuoi; }
    public void setLanDangNhapCuoi(String lanDangNhapCuoi) { this.LanDangNhapCuoi = lanDangNhapCuoi; }
    public boolean getTrangThai() { return TrangThai; }
    public void setTrangThai(boolean trangThai) { this.TrangThai = trangThai; }
}