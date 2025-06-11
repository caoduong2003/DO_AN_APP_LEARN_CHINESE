package com.example.app_learn_chinese_2025.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class BaiGiang implements Serializable {
    @SerializedName("id")
    private long ID;

    @SerializedName("maBaiGiang")
    private String MaBaiGiang;

    @SerializedName("tieuDe")
    private String TieuDe;

    @SerializedName("moTa")
    private String MoTa;

    @SerializedName("noiDung")
    private String NoiDung;

    @SerializedName("ngayTao")
    private Date NgayTao;

    @SerializedName("ngayCapNhat")
    private Date NgayCapNhat;

    @SerializedName("giangVienID")
    private long GiangVienID;

    @SerializedName("loaiBaiGiang")
    private LoaiBaiGiang LoaiBaiGiang;

    @SerializedName("capDoHSK")
    private CapDoHSK CapDoHSK;

    @SerializedName("chuDe")
    private ChuDe ChuDe;

    @SerializedName("luotXem")
    private int LuotXem;

    @SerializedName("thoiLuong")
    private int ThoiLuong;

    @SerializedName("hinhAnh")
    private String HinhAnh;

    @SerializedName("videoURL")
    private String VideoURL;

    @SerializedName("audioURL")
    private String AudioURL;

    @SerializedName("trangThai")
    private boolean TrangThai;

    @SerializedName("laBaiGiangGoi")
    private boolean LaBaiGiangGoi;

    // Constructor mặc định
    public BaiGiang() {
    }

    // Constructor đầy đủ
    public BaiGiang(long ID, String maBaiGiang, String tieuDe, String moTa, String noiDung,
                    Date ngayTao, Date ngayCapNhat, long giangVienID,
                    LoaiBaiGiang loaiBaiGiang, CapDoHSK capDoHSK, ChuDe chuDe,
                    int luotXem, int thoiLuong, String hinhAnh, String videoURL,
                    String audioURL, boolean trangThai, boolean laBaiGiangGoi) {
        this.ID = ID;
        this.MaBaiGiang = maBaiGiang;
        this.TieuDe = tieuDe;
        this.MoTa = moTa;
        this.NoiDung = noiDung;
        this.NgayTao = ngayTao;
        this.NgayCapNhat = ngayCapNhat;
        this.GiangVienID = giangVienID;
        this.LoaiBaiGiang = loaiBaiGiang;
        this.CapDoHSK = capDoHSK;
        this.ChuDe = chuDe;
        this.LuotXem = luotXem;
        this.ThoiLuong = thoiLuong;
        this.HinhAnh = hinhAnh;
        this.VideoURL = videoURL;
        this.AudioURL = audioURL;
        this.TrangThai = trangThai;
        this.LaBaiGiangGoi = laBaiGiangGoi;
    }

    // Getters và Setters (giữ nguyên tên PascalCase để tương thích với code hiện tại)
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getMaBaiGiang() {
        return MaBaiGiang;
    }

    public void setMaBaiGiang(String maBaiGiang) {
        this.MaBaiGiang = maBaiGiang;
    }

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.TieuDe = tieuDe;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        this.MoTa = moTa;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        this.NoiDung = noiDung;
    }

    public Date getNgayTao() {
        return NgayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.NgayTao = ngayTao;
    }

    public Date getNgayCapNhat() {
        return NgayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.NgayCapNhat = ngayCapNhat;
    }

    public long getGiangVienID() {
        return GiangVienID;
    }

    public void setGiangVienID(long giangVienID) {
        this.GiangVienID = giangVienID;
    }

    public LoaiBaiGiang getLoaiBaiGiang() {
        return LoaiBaiGiang;
    }

    public void setLoaiBaiGiang(LoaiBaiGiang loaiBaiGiang) {
        this.LoaiBaiGiang = loaiBaiGiang;
    }

    public CapDoHSK getCapDoHSK() {
        return CapDoHSK;
    }

    public void setCapDoHSK(CapDoHSK capDoHSK) {
        this.CapDoHSK = capDoHSK;
    }

    public ChuDe getChuDe() {
        return ChuDe;
    }

    public void setChuDe(ChuDe chuDe) {
        this.ChuDe = chuDe;
    }

    public int getLuotXem() {
        return LuotXem;
    }

    public void setLuotXem(int luotXem) {
        this.LuotXem = luotXem;
    }

    public int getThoiLuong() {
        return ThoiLuong;
    }

    public void setThoiLuong(int thoiLuong) {
        this.ThoiLuong = thoiLuong;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.HinhAnh = hinhAnh;
    }

    public String getVideoURL() {
        return VideoURL;
    }

    public void setVideoURL(String videoURL) {
        this.VideoURL = videoURL;
    }

    public String getAudioURL() {
        return AudioURL;
    }

    public void setAudioURL(String audioURL) {
        this.AudioURL = audioURL;
    }

    public boolean isTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.TrangThai = trangThai;
    }

    public boolean isLaBaiGiangGoi() {
        return LaBaiGiangGoi;
    }

    public void setLaBaiGiangGoi(boolean laBaiGiangGoi) {
        this.LaBaiGiangGoi = laBaiGiangGoi;
    }
}