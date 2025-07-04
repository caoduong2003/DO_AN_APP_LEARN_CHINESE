package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CauHoi {
    @SerializedName("id")
    private Long id;

    @SerializedName("noiDung")
    private String noiDung;

    @SerializedName("loaiCauHoi")
    private Integer loaiCauHoi;

    @SerializedName("hinhAnh")
    private String hinhAnh;

    @SerializedName("audioURL")
    private String audioURL;

    @SerializedName("thuTu")
    private Integer thuTu;

    @SerializedName("diemSo")
    private Float diemSo;

    @SerializedName("dapAnList")
    private List<DapAn> dapAnList;

    // Constructor
    public CauHoi() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public Integer getLoaiCauHoi() { return loaiCauHoi; }
    public void setLoaiCauHoi(Integer loaiCauHoi) { this.loaiCauHoi = loaiCauHoi; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getAudioURL() { return audioURL; }
    public void setAudioURL(String audioURL) { this.audioURL = audioURL; }

    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }

    public Float getDiemSo() { return diemSo; }
    public void setDiemSo(Float diemSo) { this.diemSo = diemSo; }

    public List<DapAn> getDapAnList() { return dapAnList; }
    public void setDapAnList(List<DapAn> dapAnList) { this.dapAnList = dapAnList; }

    // Utility methods
    public boolean hasImage() {
        return hinhAnh != null && !hinhAnh.trim().isEmpty();
    }

    public boolean hasAudio() {
        return audioURL != null && !audioURL.trim().isEmpty();
    }

    public boolean isMultipleChoice() {
        return loaiCauHoi != null && loaiCauHoi == 0;
    }

    public boolean isTrueFalse() {
        return loaiCauHoi != null && loaiCauHoi == 1;
    }

    public int getAnswerCount() {
        return dapAnList != null ? dapAnList.size() : 0;
    }
}