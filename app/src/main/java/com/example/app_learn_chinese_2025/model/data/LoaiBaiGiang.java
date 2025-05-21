package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class LoaiBaiGiang implements Serializable {
    private int ID;
    private String TenLoai;
    private String MoTa;

    // Constructor mặc định
    public LoaiBaiGiang() {
    }

    // Constructor đầy đủ
    public LoaiBaiGiang(int ID, String tenLoai, String moTa) {
        this.ID = ID;
        this.TenLoai = tenLoai;
        this.MoTa = moTa;
    }

    // Getters và Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTenLoai() {
        return TenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.TenLoai = tenLoai;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        this.MoTa = moTa;
    }
}