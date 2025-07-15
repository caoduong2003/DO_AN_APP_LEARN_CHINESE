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
    @Override
    public String toString() {
        return TenLoai; // Hiển thị tên loại trong Spinner
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LoaiBaiGiang loaiBaiGiang = (LoaiBaiGiang) obj;
        return ID == loaiBaiGiang.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}