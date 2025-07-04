package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class BaiTap {
    @SerializedName("id")
    private Long id;

    @SerializedName("tieuDe")
    private String tieuDe;

    @SerializedName("moTa")
    private String moTa;

    @SerializedName("baiGiangId")
    private Long baiGiangId;

    @SerializedName("baiGiangTieuDe")
    private String baiGiangTieuDe;

    @SerializedName("capDoHSKId")
    private Integer capDoHSKId;

    @SerializedName("capDoHSKTen")
    private String capDoHSKTen;

    @SerializedName("chuDeId")
    private Integer chuDeId;

    @SerializedName("chuDeTen")
    private String chuDeTen;

    @SerializedName("thoiGianLam")
    private Integer thoiGianLam;

    @SerializedName("soCauHoi")
    private Integer soCauHoi;

    @SerializedName("diemToiDa")
    private Float diemToiDa;

    @SerializedName("diemCaoNhat")
    private Float diemCaoNhat;

    @SerializedName("soLanLam")
    private Integer soLanLam;

    @SerializedName("ngayTao")
    private Date ngayTao;

    @SerializedName("ngayCapNhat")
    private Date ngayCapNhat;

    @SerializedName("cauHoiList")
    private List<CauHoi> cauHoiList;

    // Constructors
    public BaiTap() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public Long getBaiGiangId() { return baiGiangId; }
    public void setBaiGiangId(Long baiGiangId) { this.baiGiangId = baiGiangId; }

    public String getBaiGiangTieuDe() { return baiGiangTieuDe; }
    public void setBaiGiangTieuDe(String baiGiangTieuDe) { this.baiGiangTieuDe = baiGiangTieuDe; }

    public Integer getCapDoHSKId() { return capDoHSKId; }
    public void setCapDoHSKId(Integer capDoHSKId) { this.capDoHSKId = capDoHSKId; }

    public String getCapDoHSKTen() { return capDoHSKTen; }
    public void setCapDoHSKTen(String capDoHSKTen) { this.capDoHSKTen = capDoHSKTen; }

    public Integer getChuDeId() { return chuDeId; }
    public void setChuDeId(Integer chuDeId) { this.chuDeId = chuDeId; }

    public String getChuDeTen() { return chuDeTen; }
    public void setChuDeTen(String chuDeTen) { this.chuDeTen = chuDeTen; }

    public Integer getThoiGianLam() { return thoiGianLam; }
    public void setThoiGianLam(Integer thoiGianLam) { this.thoiGianLam = thoiGianLam; }

    public Integer getSoCauHoi() { return soCauHoi; }
    public void setSoCauHoi(Integer soCauHoi) { this.soCauHoi = soCauHoi; }

    public Float getDiemToiDa() { return diemToiDa; }
    public void setDiemToiDa(Float diemToiDa) { this.diemToiDa = diemToiDa; }

    public Float getDiemCaoNhat() { return diemCaoNhat; }
    public void setDiemCaoNhat(Float diemCaoNhat) { this.diemCaoNhat = diemCaoNhat; }

    public Integer getSoLanLam() { return soLanLam; }
    public void setSoLanLam(Integer soLanLam) { this.soLanLam = soLanLam; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public List<CauHoi> getCauHoiList() { return cauHoiList; }
    public void setCauHoiList(List<CauHoi> cauHoiList) { this.cauHoiList = cauHoiList; }

    // Utility methods
    public boolean hasDiemCaoNhat() {
        return diemCaoNhat != null && diemCaoNhat > 0;
    }

    public boolean hasBeenTaken() {
        return soLanLam != null && soLanLam > 0;
    }

    public String getFormattedTime() {
        if (thoiGianLam == null) return "Không giới hạn";
        return thoiGianLam + " phút";
    }

    public String getFormattedQuestions() {
        if (soCauHoi == null) return "0 câu";
        return soCauHoi + " câu";
    }

    public String getFormattedScore() {
        if (diemToiDa == null) return "0 điểm";
        return diemToiDa + " điểm";
    }
}