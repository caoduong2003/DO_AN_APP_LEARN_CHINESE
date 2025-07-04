package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;

public class DapAn {
    @SerializedName("id")
    private Long id;

    @SerializedName("noiDung")
    private String noiDung;

    @SerializedName("thuTu")
    private Integer thuTu;

    // Constructor
    public DapAn() {}

    public DapAn(Long id, String noiDung, Integer thuTu) {
        this.id = id;
        this.noiDung = noiDung;
        this.thuTu = thuTu;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }

    // Utility methods
    public boolean hasContent() {
        return noiDung != null && !noiDung.trim().isEmpty();
    }

    public String getOptionLabel() {
        if (thuTu == null) return "";
        switch (thuTu) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            default: return String.valueOf(thuTu);
        }
    }
}