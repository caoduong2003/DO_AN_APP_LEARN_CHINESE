package com.example.app_learn_chinese_2025.model.data;

import com.example.app_learn_chinese_2025.util.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BaiGiang {
    @SerializedName("id")
    private Long ID;

    @SerializedName("tieuDe")
    private String tieuDe;

    @SerializedName("moTa")
    private String moTa;

    @SerializedName("videoURL")
    private String videoURL;

    @SerializedName("thumbnailURL")
    private String thumbnailURL;

    @SerializedName("laBaiGiangGoi")
    private boolean laBaiGiangGoi;

    @SerializedName("published")
    private boolean published;

    @SerializedName("trangThai")
    private boolean trangThai;

    @SerializedName("luotXem")
    private int luotXem;

    @SerializedName("ngayTao")
    private Date ngayTao;

    @SerializedName("ngayCapNhat")
    private Date ngayCapNhat;

    @SerializedName("giangVien")
    private User giangVien;

    @SerializedName("capDoHSK")
    private CapDoHSK capDoHSK;

    @SerializedName("chuDe")
    private ChuDe chuDe;

    @SerializedName("loaiBaiGiang")
    private LoaiBaiGiang loaiBaiGiang;

    @SerializedName("noiDung")
    private String noiDung;

    @SerializedName("thoiLuong")
    private int thoiLuong;

    @SerializedName("hinhAnh")
    private String hinhAnh;

    @SerializedName("audioURL")
    private String audioURL;

    // Getters and Setters
    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public boolean isLaBaiGiangGoi() {
        return laBaiGiangGoi;
    }

    public void setLaBaiGiangGoi(boolean laBaiGiangGoi) {
        this.laBaiGiangGoi = laBaiGiangGoi;
    }

    public boolean isPublished() {
        return trangThai;
    }


    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(int luotXem) {
        this.luotXem = luotXem;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public User getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(User giangVien) {
        this.giangVien = giangVien;
    }

    public CapDoHSK getCapDoHSK() {
        return capDoHSK;
    }

    public void setCapDoHSK(CapDoHSK capDoHSK) {
        this.capDoHSK = capDoHSK;
    }

    public ChuDe getChuDe() {
        return chuDe;
    }

    public void setChuDe(ChuDe chuDe) {
        this.chuDe = chuDe;
    }

    public LoaiBaiGiang getLoaiBaiGiang() {
        return loaiBaiGiang;
    }

    public void setLoaiBaiGiang(LoaiBaiGiang loaiBaiGiang) {
        this.loaiBaiGiang = loaiBaiGiang;
    }


    public String getNoiDung() {
        return noiDung;
    }

    public int getThoiLuong() {
        return thoiLuong;
    }

    public Long getId() {
        return ID;
    }

    public void setId(Long id) {
        this.ID = id;
    }

    public String getHinhAnh() {
        return thumbnailURL;
    }

    public boolean hasVideo() {
        return videoURL != null && !videoURL.isEmpty();
    }

    public String getFullVideoURL() {
        if (hasVideo()) {
            return Constants.getBaseUrl() + videoURL;
        }
        return null;
    }

    public String getVideoUrl() {
      return videoURL;
    }
}