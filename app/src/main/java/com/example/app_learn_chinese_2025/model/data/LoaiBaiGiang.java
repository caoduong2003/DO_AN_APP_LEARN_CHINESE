package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LoaiBaiGiang implements Serializable {
    @SerializedName("id")
    private int ID;

    @SerializedName("tenLoai")
    private String TenLoai;

    @SerializedName("moTa")
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