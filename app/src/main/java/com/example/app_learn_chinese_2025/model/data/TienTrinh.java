package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;
import java.util.Date;

public class TienTrinh implements Serializable {
    private long id;
    private User nguoiDung;  // User object
    private BaiGiang baiGiang;  // BaiGiang object
    private Date ngayBatDau;
    private Date ngayHoanThanh;
    private int trangThai;  // 0: Chưa học, 1: Đang học, 2: Đã hoàn thành
    private int tienDo;     // 0-100%
    private float diemSo;   // Điểm số nếu có
    private String ghiChu;

    // Constructor mặc định
    public TienTrinh() {
        this.trangThai = 0;  // Mặc định chưa học
        this.tienDo = 0;     // Mặc định 0%
    }

    // Constructor đầy đủ
    public TienTrinh(long id, User nguoiDung, BaiGiang baiGiang, Date ngayBatDau,
                     Date ngayHoanThanh, int trangThai, int tienDo, float diemSo, String ghiChu) {
        this.id = id;
        this.nguoiDung = nguoiDung;
        this.baiGiang = baiGiang;
        this.ngayBatDau = ngayBatDau;
        this.ngayHoanThanh = ngayHoanThanh;
        this.trangThai = trangThai;
        this.tienDo = tienDo;
        this.diemSo = diemSo;
        this.ghiChu = ghiChu;
    }

    // Getters và Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(User nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public BaiGiang getBaiGiang() {
        return baiGiang;
    }

    public void setBaiGiang(BaiGiang baiGiang) {
        this.baiGiang = baiGiang;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayHoanThanh() {
        return ngayHoanThanh;
    }

    public void setNgayHoanThanh(Date ngayHoanThanh) {
        this.ngayHoanThanh = ngayHoanThanh;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getTienDo() {
        return tienDo;
    }

    public void setTienDo(int tienDo) {
        this.tienDo = tienDo;
    }

    public float getDiemSo() {
        return diemSo;
    }

    public void setDiemSo(float diemSo) {
        this.diemSo = diemSo;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Utility methods
    public boolean isDaHoanThanh() {
        return trangThai == 2;  // Trạng thái 2 = Đã hoàn thành
    }

    public void setDaHoanThanh(boolean daHoanThanh) {
        this.trangThai = daHoanThanh ? 2 : (tienDo > 0 ? 1 : 0);
    }

    // Compatibility với code cũ
    public User getUser() {
        return nguoiDung;
    }

    public void setUser(User user) {
        this.nguoiDung = user;
    }

    public Date getNgayCapNhat() {
        return ngayHoanThanh != null ? ngayHoanThanh : ngayBatDau;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        // Compatibility method - không làm gì
    }
}