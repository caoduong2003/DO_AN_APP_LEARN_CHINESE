package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class KetQuaBaiTap {
    @SerializedName("id")
    private Long id;

    @SerializedName("baiTapId")
    private Long baiTapId;

    @SerializedName("baiTapTieuDe")
    private String baiTapTieuDe;

    @SerializedName("diemSo")
    private Float diemSo;

    @SerializedName("diemToiDa")
    private Float diemToiDa;

    @SerializedName("thoiGianLam")
    private Integer thoiGianLam;

    @SerializedName("soCauDung")
    private Integer soCauDung;

    @SerializedName("tongSoCau")
    private Integer tongSoCau;

    @SerializedName("ngayLamBai")
    private Date ngayLamBai;

    @SerializedName("trangThai")
    private Integer trangThai;

    @SerializedName("tiLeDung")
    private Float tiLeDung;

    @SerializedName("xepLoai")
    private String xepLoai;

    // Constructor
    public KetQuaBaiTap() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBaiTapId() { return baiTapId; }
    public void setBaiTapId(Long baiTapId) { this.baiTapId = baiTapId; }

    public String getBaiTapTieuDe() { return baiTapTieuDe; }
    public void setBaiTapTieuDe(String baiTapTieuDe) { this.baiTapTieuDe = baiTapTieuDe; }

    public Float getDiemSo() { return diemSo; }
    public void setDiemSo(Float diemSo) { this.diemSo = diemSo; }

    public Float getDiemToiDa() { return diemToiDa; }
    public void setDiemToiDa(Float diemToiDa) { this.diemToiDa = diemToiDa; }

    public Integer getThoiGianLam() { return thoiGianLam; }
    public void setThoiGianLam(Integer thoiGianLam) { this.thoiGianLam = thoiGianLam; }

    public Integer getSoCauDung() { return soCauDung; }
    public void setSoCauDung(Integer soCauDung) { this.soCauDung = soCauDung; }

    public Integer getTongSoCau() { return tongSoCau; }
    public void setTongSoCau(Integer tongSoCau) { this.tongSoCau = tongSoCau; }

    public Date getNgayLamBai() { return ngayLamBai; }
    public void setNgayLamBai(Date ngayLamBai) { this.ngayLamBai = ngayLamBai; }

    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }

    public Float getTiLeDung() { return tiLeDung; }
    public void setTiLeDung(Float tiLeDung) { this.tiLeDung = tiLeDung; }

    public String getXepLoai() { return xepLoai; }
    public void setXepLoai(String xepLoai) { this.xepLoai = xepLoai; }

    // Utility methods
    public String getFormattedScore() {
        if (diemSo == null || diemToiDa == null) return "0/0";
        return String.format("%.1f/%.1f", diemSo, diemToiDa);
    }

    public String getFormattedTime() {
        if (thoiGianLam == null) return "0 phút";
        int minutes = thoiGianLam / 60;
        int seconds = thoiGianLam % 60;
        if (seconds == 0) {
            return minutes + " phút";
        }
        return minutes + " phút " + seconds + " giây";
    }

    public String getFormattedPercentage() {
        if (tiLeDung == null) return "0%";
        return String.format("%.1f%%", tiLeDung);
    }

    public String getFormattedCorrectAnswers() {
        if (soCauDung == null || tongSoCau == null) return "0/0";
        return soCauDung + "/" + tongSoCau + " câu";
    }

    public boolean isPassed() {
        return tiLeDung != null && tiLeDung >= 60.0f;
    }

    public boolean isExcellent() {
        return tiLeDung != null && tiLeDung >= 90.0f;
    }


}