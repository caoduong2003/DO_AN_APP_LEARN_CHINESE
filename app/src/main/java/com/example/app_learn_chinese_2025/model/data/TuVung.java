package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class TuVung implements Serializable {
    private long id;
    private BaiGiang baiGiang;
    private String tiengTrung;
    private String phienAm;
    private String tiengViet;
    private String loaiTu;
    private String viDu;
    private String hinhAnh;
    private String audioURL;
    private String ghiChu;
    private CapDoHSK capDoHSK;

    // Constructor mặc định
    public TuVung() {
    }

    // Constructor đầy đủ
    public TuVung(long id, BaiGiang baiGiang, String tiengTrung, String phienAm,
                  String tiengViet, String loaiTu, String viDu, String hinhAnh,
                  String audioURL, String ghiChu, CapDoHSK capDoHSK) {
        this.id = id;
        this.baiGiang = baiGiang;
        this.tiengTrung = tiengTrung;
        this.phienAm = phienAm;
        this.tiengViet = tiengViet;
        this.loaiTu = loaiTu;
        this.viDu = viDu;
        this.hinhAnh = hinhAnh;
        this.audioURL = audioURL;
        this.ghiChu = ghiChu;
        this.capDoHSK = capDoHSK;
    }

    // Constructor cơ bản (chỉ với thông tin quan trọng nhất)
    public TuVung(String tiengTrung, String phienAm, String tiengViet) {
        this.tiengTrung = tiengTrung;
        this.phienAm = phienAm;
        this.tiengViet = tiengViet;
    }

    // Getters và Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BaiGiang getBaiGiang() {
        return baiGiang;
    }

    public void setBaiGiang(BaiGiang baiGiang) {
        this.baiGiang = baiGiang;
    }

    public String getTiengTrung() {
        return tiengTrung;
    }

    public void setTiengTrung(String tiengTrung) {
        this.tiengTrung = tiengTrung;
    }

    public String getPhienAm() {
        return phienAm;
    }

    public void setPhienAm(String phienAm) {
        this.phienAm = phienAm;
    }

    public String getTiengViet() {
        return tiengViet;
    }

    public void setTiengViet(String tiengViet) {
        this.tiengViet = tiengViet;
    }

    public String getLoaiTu() {
        return loaiTu;
    }

    public void setLoaiTu(String loaiTu) {
        this.loaiTu = loaiTu;
    }

    public String getViDu() {
        return viDu;
    }

    public void setViDu(String viDu) {
        this.viDu = viDu;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public CapDoHSK getCapDoHSK() {
        return capDoHSK;
    }

    public void setCapDoHSK(CapDoHSK capDoHSK) {
        this.capDoHSK = capDoHSK;
    }

    // Utility methods

    /**
     * Kiểm tra xem từ vựng có đầy đủ thông tin cơ bản không
     * @return true nếu có đầy đủ tiếng Trung và tiếng Việt
     */
    public boolean isValid() {
        return tiengTrung != null && !tiengTrung.trim().isEmpty() &&
                tiengViet != null && !tiengViet.trim().isEmpty();
    }

    /**
     * Kiểm tra xem từ vựng có file âm thanh không
     * @return true nếu có URL âm thanh
     */
    public boolean hasAudio() {
        return audioURL != null && !audioURL.trim().isEmpty();
    }

    /**
     * Kiểm tra xem từ vựng có hình ảnh không
     * @return true nếu có URL hình ảnh
     */
    public boolean hasImage() {
        return hinhAnh != null && !hinhAnh.trim().isEmpty();
    }

    /**
     * Kiểm tra xem từ vựng có ví dụ không
     * @return true nếu có ví dụ
     */
    public boolean hasExample() {
        return viDu != null && !viDu.trim().isEmpty();
    }

    /**
     * Lấy tên hiển thị của loại từ
     * @return tên loại từ hoặc "Chưa phân loại" nếu không có
     */
    public String getLoaiTuDisplay() {
        return (loaiTu != null && !loaiTu.trim().isEmpty()) ? loaiTu : "Chưa phân loại";
    }

    /**
     * Lấy cấp độ HSK hiển thị
     * @return chuỗi hiển thị cấp độ HSK
     */
    public String getCapDoHSKDisplay() {
        if (capDoHSK != null) {
            return "HSK " + capDoHSK.getCapDo();
        }
        return "Chưa phân cấp";
    }

    @Override
    public String toString() {
        return "TuVung{" +
                "id=" + id +
                ", tiengTrung='" + tiengTrung + '\'' +
                ", phienAm='" + phienAm + '\'' +
                ", tiengViet='" + tiengViet + '\'' +
                ", loaiTu='" + loaiTu + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TuVung tuVung = (TuVung) obj;
        return id == tuVung.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}