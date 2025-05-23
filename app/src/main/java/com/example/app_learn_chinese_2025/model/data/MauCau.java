package com.example.app_learn_chinese_2025.model.data;

import java.io.Serializable;

public class MauCau implements Serializable {
    private long id;
    private BaiGiang baiGiang;
    private String tiengTrung;
    private String phienAm;
    private String tiengViet;
    private String audioURL;
    private String ghiChu;

    public MauCau() {
    }

    public MauCau(long id, BaiGiang baiGiang, String tiengTrung, String phienAm, String tiengViet, String audioURL, String ghiChu) {
        this.id = id;
        this.baiGiang = baiGiang;
        this.tiengTrung = tiengTrung;
        this.phienAm = phienAm;
        this.tiengViet = tiengViet;
        this.audioURL = audioURL;
        this.ghiChu = ghiChu;
    }

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
}