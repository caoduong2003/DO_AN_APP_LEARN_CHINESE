package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;
import java.util.Date;

public class TienTrinh implements Serializable {
    private long id;
    private User user;
    private BaiGiang baiGiang;
    private int tienDo; // 0-100 percentage
    private boolean daHoanThanh;
    private Date ngayBatDau;
    private Date ngayCapNhat;

    public TienTrinh() {
    }

    public TienTrinh(long id, User user, BaiGiang baiGiang, int tienDo, boolean daHoanThanh, Date ngayBatDau, Date ngayCapNhat) {
        this.id = id;
        this.user = user;
        this.baiGiang = baiGiang;
        this.tienDo = tienDo;
        this.daHoanThanh = daHoanThanh;
        this.ngayBatDau = ngayBatDau;
        this.ngayCapNhat = ngayCapNhat;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BaiGiang getBaiGiang() {
        return baiGiang;
    }

    public void setBaiGiang(BaiGiang baiGiang) {
        this.baiGiang = baiGiang;
    }

    public int getTienDo() {
        return tienDo;
    }

    public void setTienDo(int tienDo) {
        this.tienDo = tienDo;
    }

    public boolean isDaHoanThanh() {
        return daHoanThanh;
    }

    public void setDaHoanThanh(boolean daHoanThanh) {
        this.daHoanThanh = daHoanThanh;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}