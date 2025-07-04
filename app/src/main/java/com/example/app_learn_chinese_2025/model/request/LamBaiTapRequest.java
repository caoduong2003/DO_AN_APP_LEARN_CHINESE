package com.example.app_learn_chinese_2025.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LamBaiTapRequest {
    @SerializedName("baiTapId")
    private Long baiTapId;

    @SerializedName("thoiGianLam")
    private Integer thoiGianLam;

    @SerializedName("danhSachTraLoi")
    private List<TraLoiRequest> danhSachTraLoi;

    // Constructor
    public LamBaiTapRequest() {}

    public LamBaiTapRequest(Long baiTapId, Integer thoiGianLam, List<TraLoiRequest> danhSachTraLoi) {
        this.baiTapId = baiTapId;
        this.thoiGianLam = thoiGianLam;
        this.danhSachTraLoi = danhSachTraLoi;
    }

    // Getters and Setters
    public Long getBaiTapId() { return baiTapId; }
    public void setBaiTapId(Long baiTapId) { this.baiTapId = baiTapId; }

    public Integer getThoiGianLam() { return thoiGianLam; }
    public void setThoiGianLam(Integer thoiGianLam) { this.thoiGianLam = thoiGianLam; }

    public List<TraLoiRequest> getDanhSachTraLoi() { return danhSachTraLoi; }
    public void setDanhSachTraLoi(List<TraLoiRequest> danhSachTraLoi) { this.danhSachTraLoi = danhSachTraLoi; }

    // Inner class cho từng câu trả lời
    public static class TraLoiRequest {
        @SerializedName("cauHoiId")
        private Long cauHoiId;

        @SerializedName("dapAnId")
        private Long dapAnId;

        // Constructor
        public TraLoiRequest() {}

        public TraLoiRequest(Long cauHoiId, Long dapAnId) {
            this.cauHoiId = cauHoiId;
            this.dapAnId = dapAnId;
        }

        // Getters and Setters
        public Long getCauHoiId() { return cauHoiId; }
        public void setCauHoiId(Long cauHoiId) { this.cauHoiId = cauHoiId; }

        public Long getDapAnId() { return dapAnId; }
        public void setDapAnId(Long dapAnId) { this.dapAnId = dapAnId; }
    }
}