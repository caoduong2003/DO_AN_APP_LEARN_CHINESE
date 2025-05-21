package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class CapDoHSK implements Serializable {
    private int ID;
    private int CapDo;
    private String TenCapDo;
    private String MoTa;

    // Constructor mặc định
    public CapDoHSK() {
    }

    // Constructor đầy đủ
    public CapDoHSK(int ID, int capDo, String tenCapDo, String moTa) {
        this.ID = ID;
        this.CapDo = capDo;
        this.TenCapDo = tenCapDo;
        this.MoTa = moTa;
    }

    // Getters và Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCapDo() {
        return CapDo;
    }

    public void setCapDo(int capDo) {
        this.CapDo = capDo;
    }

    public String getTenCapDo() {
        return TenCapDo;
    }

    public void setTenCapDo(String tenCapDo) {
        this.TenCapDo = tenCapDo;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        this.MoTa = moTa;
    }
}