package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CapDoHSK implements Serializable {
    @SerializedName("id")
    private int ID;

    @SerializedName("capDo")
    private int CapDo;

    @SerializedName("tenCapDo")
    private String TenCapDo;

    @SerializedName("moTa")
    private String MoTa;

    // Constructor mặc định
    public CapDoHSK() {
    }

    // Constructor đơn giản cho giá trị mặc định
    public CapDoHSK(int id, String tenCapDo) {
        this.ID = id;
        this.CapDo = id; // Giả sử capDo bằng id (ví dụ: HSK 1 -> capDo = 1)
        this.TenCapDo = tenCapDo;
        this.MoTa = "Cấp độ " + tenCapDo; // Mô tả mặc định
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