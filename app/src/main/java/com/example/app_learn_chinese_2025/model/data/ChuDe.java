// ChuDe.java
package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ChuDe implements Serializable {
    @SerializedName("id")
    private int ID;

    @SerializedName("tenChuDe")
    private String TenChuDe;

    @SerializedName("moTa")
    private String MoTa;

    @SerializedName("hinhAnh")
    private String HinhAnh;

    // Constructor mặc định
    public ChuDe() {
    }

    // Constructor đầy đủ
    public ChuDe(int ID, String tenChuDe, String moTa, String hinhAnh) {
        this.ID = ID;
        this.TenChuDe = tenChuDe;
        this.MoTa = moTa;
        this.HinhAnh = hinhAnh;
    }

    // Getters và Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTenChuDe() {
        return TenChuDe;
    }

    public void setTenChuDe(String tenChuDe) {
        this.TenChuDe = tenChuDe;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        this.MoTa = moTa;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.HinhAnh = hinhAnh;
    }
}