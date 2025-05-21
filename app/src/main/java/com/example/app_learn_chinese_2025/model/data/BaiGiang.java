package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;
import java.util.Date;

public class BaiGiang implements Serializable {
    private long ID;
    private String MaBaiGiang;
    private String TieuDe;
    private String MoTa;
    private String NoiDung;
    private Date NgayTao;
    private Date NgayCapNhat;
    private long GiangVienID;
    private LoaiBaiGiang LoaiBaiGiang;
    private CapDoHSK CapDoHSK;
    private ChuDe ChuDe;
    private int LuotXem;
    private int ThoiLuong;
    private String HinhAnh;
    private String VideoURL;
    private String AudioURL;
    private boolean TrangThai;
    private boolean LaBaiGiangGoi;

    // Các trường bổ sung không lưu trong database
    private String TenGiangVien;

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

    // Getters và Setters
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

    public String getTenGiangVien() {
        return TenGiangVien;
    }

    public void setTenGiangVien(String tenGiangVien) {
        this.TenGiangVien = tenGiangVien;
    }
}