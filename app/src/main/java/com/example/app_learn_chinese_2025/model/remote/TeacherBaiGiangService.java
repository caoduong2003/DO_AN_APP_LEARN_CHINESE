package com.example.app_learn_chinese_2025.model.remote;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * 🎯 Service interface cho Teacher BaiGiang CRUD operations
 * Kết nối với backend API: /api/teacher-baigiang
 */
public interface TeacherBaiGiangService {

    // ===== GET OPERATIONS =====

    /**
     * Lấy danh sách bài giảng của giáo viên với phân trang
     * GET /api/teacher-baigiang
     */
    @GET("api/teacher-baigiang")
    Call<TeacherBaiGiangResponse.PageResponse> getMyBaiGiangs(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir,
            @Query("search") String search,
            @Query("capDoHSKId") Integer capDoHSKId,
            @Query("chuDeId") Integer chuDeId,
            @Query("loaiBaiGiangId") Integer loaiBaiGiangId,
            @Query("trangThai") Boolean trangThai
    );

    /**
     * Lấy chi tiết bài giảng
     * GET /api/teacher-baigiang/{id}
     */
    @GET("api/teacher-baigiang/{id}")
    Call<TeacherBaiGiangResponse.DetailResponse> getBaiGiangDetail(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Lấy thống kê bài giảng của giáo viên
     * GET /api/teacher-baigiang/statistics
     */
    @GET("api/teacher-baigiang/statistics")
    Call<TeacherBaiGiangResponse.StatsResponse> getBaiGiangStatistics(
            @Header("Authorization") String token
    );

    /**
     * Tìm kiếm bài giảng
     * GET /api/teacher-baigiang/search
     */
    @GET("api/teacher-baigiang/search")
    Call<List<TeacherBaiGiangResponse.SimpleResponse>> searchBaiGiangs(
            @Header("Authorization") String token,
            @Query("keyword") String keyword,
            @Query("limit") int limit
    );

    // ===== POST OPERATIONS =====

    /**
     * Tạo bài giảng mới
     * POST /api/teacher-baigiang
     */
    @POST("api/teacher-baigiang")
    Call<TeacherBaiGiangResponse.SimpleResponse> createBaiGiang(
            @Header("Authorization") String token,
            @Body TeacherBaiGiangRequest.CreateRequest request
    );

    /**
     * Nhân bản bài giảng
     * POST /api/teacher-baigiang/{id}/duplicate
     */
    @POST("api/teacher-baigiang/{id}/duplicate")
    Call<TeacherBaiGiangResponse.SimpleResponse> duplicateBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body Map<String, String> options
    );

    // ===== PUT OPERATIONS =====

    /**
     * Cập nhật bài giảng
     * PUT /api/teacher-baigiang/{id}
     */
    @PUT("api/teacher-baigiang/{id}")
    Call<TeacherBaiGiangResponse.SimpleResponse> updateBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body TeacherBaiGiangRequest.UpdateRequest request
    );

    // ===== PATCH OPERATIONS =====

    /**
     * Thay đổi trạng thái công khai/ẩn
     * PATCH /api/teacher-baigiang/{id}/toggle-status
     */
    @PATCH("api/teacher-baigiang/{id}/toggle-status")
    Call<TeacherBaiGiangResponse.SimpleResponse> toggleStatus(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Thay đổi trạng thái premium
     * PATCH /api/teacher-baigiang/{id}/toggle-premium
     */
    @PATCH("api/teacher-baigiang/{id}/toggle-premium")
    Call<TeacherBaiGiangResponse.SimpleResponse> togglePremium(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    // ===== DELETE OPERATIONS =====

    /**
     * Xóa bài giảng (soft delete)
     * DELETE /api/teacher-baigiang/{id}
     */
    @DELETE("api/teacher-baigiang/{id}")
    Call<Map<String, String>> deleteBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    // ===== NESTED CLASSES FOR REQUESTS =====

    class TeacherBaiGiangRequest {

        public static class CreateRequest {
            private String tieuDe;
            private String moTa;
            private String noiDung;
            private Integer thoiLuong;
            private String hinhAnh;
            private String videoURL;
            private String audioURL;
            private Boolean trangThai;
            private Boolean laBaiGiangGoi;
            private Integer capDoHSKId;
            private Integer chuDeId;
            private Integer loaiBaiGiangId;

            // Constructors
            public CreateRequest() {}

            public CreateRequest(String tieuDe, String moTa, String noiDung) {
                this.tieuDe = tieuDe;
                this.moTa = moTa;
                this.noiDung = noiDung;
                this.trangThai = true;
                this.laBaiGiangGoi = false;
            }

            // Getters and Setters
            public String getTieuDe() { return tieuDe; }
            public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

            public String getMoTa() { return moTa; }
            public void setMoTa(String moTa) { this.moTa = moTa; }

            public String getNoiDung() { return noiDung; }
            public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

            public Integer getThoiLuong() { return thoiLuong; }
            public void setThoiLuong(Integer thoiLuong) { this.thoiLuong = thoiLuong; }

            public String getHinhAnh() { return hinhAnh; }
            public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

            public String getVideoURL() { return videoURL; }
            public void setVideoURL(String videoURL) { this.videoURL = videoURL; }

            public String getAudioURL() { return audioURL; }
            public void setAudioURL(String audioURL) { this.audioURL = audioURL; }

            public Boolean getTrangThai() { return trangThai; }
            public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }

            public Boolean getLaBaiGiangGoi() { return laBaiGiangGoi; }
            public void setLaBaiGiangGoi(Boolean laBaiGiangGoi) { this.laBaiGiangGoi = laBaiGiangGoi; }

            public Integer getCapDoHSKId() { return capDoHSKId; }
            public void setCapDoHSKId(Integer capDoHSKId) { this.capDoHSKId = capDoHSKId; }

            public Integer getChuDeId() { return chuDeId; }
            public void setChuDeId(Integer chuDeId) { this.chuDeId = chuDeId; }

            public Integer getLoaiBaiGiangId() { return loaiBaiGiangId; }
            public void setLoaiBaiGiangId(Integer loaiBaiGiangId) { this.loaiBaiGiangId = loaiBaiGiangId; }
        }

        public static class UpdateRequest extends CreateRequest {
            // UpdateRequest có thể extend CreateRequest vì chúng có structure tương tự
            // Hoặc có thể override các field cần thiết
        }
    }

    // ===== NESTED CLASSES FOR RESPONSES =====

    class TeacherBaiGiangResponse {

        public static class PageResponse {
            private List<SimpleResponse> content;
            private int page;
            private int size;
            private int totalPages;
            private long totalElements;
            private boolean last;
            private boolean first;

            // Getters and Setters
            public List<SimpleResponse> getContent() { return content; }
            public void setContent(List<SimpleResponse> content) { this.content = content; }

            public int getPage() { return page; }
            public void setPage(int page) { this.page = page; }

            public int getSize() { return size; }
            public void setSize(int size) { this.size = size; }

            public int getTotalPages() { return totalPages; }
            public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

            public long getTotalElements() { return totalElements; }
            public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

            public boolean isLast() { return last; }
            public void setLast(boolean last) { this.last = last; }

            public boolean isFirst() { return first; }
            public void setFirst(boolean first) { this.first = first; }
        }

        public static class SimpleResponse {
            private Long id;
            private String maBaiGiang;
            private String tieuDe;
            private String moTa;
            private String ngayTao;
            private String ngayCapNhat;
            private Integer luotXem;
            private Integer thoiLuong;
            private String hinhAnh;
            private String videoURL;
            private String audioURL;
            private Boolean trangThai;
            private Boolean laBaiGiangGoi;

            // Nested objects
            private LoaiBaiGiangInfo loaiBaiGiang;
            private CapDoHSKInfo capDoHSK;
            private ChuDeInfo chuDe;
            private GiangVienInfo giangVien;

            // Getters and Setters
            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }

            public String getMaBaiGiang() { return maBaiGiang; }
            public void setMaBaiGiang(String maBaiGiang) { this.maBaiGiang = maBaiGiang; }

            public String getTieuDe() { return tieuDe; }
            public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

            public String getMoTa() { return moTa; }
            public void setMoTa(String moTa) { this.moTa = moTa; }

            public String getNgayTao() { return ngayTao; }
            public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }

            public String getNgayCapNhat() { return ngayCapNhat; }
            public void setNgayCapNhat(String ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

            public Integer getLuotXem() { return luotXem; }
            public void setLuotXem(Integer luotXem) { this.luotXem = luotXem; }

            public Integer getThoiLuong() { return thoiLuong; }
            public void setThoiLuong(Integer thoiLuong) { this.thoiLuong = thoiLuong; }

            public String getHinhAnh() { return hinhAnh; }
            public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

            public String getVideoURL() { return videoURL; }
            public void setVideoURL(String videoURL) { this.videoURL = videoURL; }

            public String getAudioURL() { return audioURL; }
            public void setAudioURL(String audioURL) { this.audioURL = audioURL; }

            public Boolean getTrangThai() { return trangThai; }
            public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }

            public Boolean getLaBaiGiangGoi() { return laBaiGiangGoi; }
            public void setLaBaiGiangGoi(Boolean laBaiGiangGoi) { this.laBaiGiangGoi = laBaiGiangGoi; }

            public LoaiBaiGiangInfo getLoaiBaiGiang() { return loaiBaiGiang; }
            public void setLoaiBaiGiang(LoaiBaiGiangInfo loaiBaiGiang) { this.loaiBaiGiang = loaiBaiGiang; }

            public CapDoHSKInfo getCapDoHSK() { return capDoHSK; }
            public void setCapDoHSK(CapDoHSKInfo capDoHSK) { this.capDoHSK = capDoHSK; }

            public ChuDeInfo getChuDe() { return chuDe; }
            public void setChuDe(ChuDeInfo chuDe) { this.chuDe = chuDe; }

            public GiangVienInfo getGiangVien() { return giangVien; }
            public void setGiangVien(GiangVienInfo giangVien) { this.giangVien = giangVien; }
        }

        public static class DetailResponse extends SimpleResponse {
            private String noiDung;

            public String getNoiDung() { return noiDung; }
            public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
        }

        public static class StatsResponse {
            private Long tongSoBaiGiang;
            private Long baiGiangCongKhai;
            private Long baiGiangGoi;
            private Long tongLuotXem;
            private Double luotXemTrungBinh;

            // Getters and Setters
            public Long getTongSoBaiGiang() { return tongSoBaiGiang; }
            public void setTongSoBaiGiang(Long tongSoBaiGiang) { this.tongSoBaiGiang = tongSoBaiGiang; }

            public Long getBaiGiangCongKhai() { return baiGiangCongKhai; }
            public void setBaiGiangCongKhai(Long baiGiangCongKhai) { this.baiGiangCongKhai = baiGiangCongKhai; }

            public Long getBaiGiangGoi() { return baiGiangGoi; }
            public void setBaiGiangGoi(Long baiGiangGoi) { this.baiGiangGoi = baiGiangGoi; }

            public Long getTongLuotXem() { return tongLuotXem; }
            public void setTongLuotXem(Long tongLuotXem) { this.tongLuotXem = tongLuotXem; }

            public Double getLuotXemTrungBinh() { return luotXemTrungBinh; }
            public void setLuotXemTrungBinh(Double luotXemTrungBinh) { this.luotXemTrungBinh = luotXemTrungBinh; }
        }

        // ===== NESTED INFO CLASSES =====

        public static class LoaiBaiGiangInfo {
            private Integer id;
            private String ten;

            public Integer getId() { return id; }
            public void setId(Integer id) { this.id = id; }

            public String getTen() { return ten; }
            public void setTen(String ten) { this.ten = ten; }
        }

        public static class CapDoHSKInfo {
            private Integer id;
            private String ten;
            private Integer capDo;

            public Integer getId() { return id; }
            public void setId(Integer id) { this.id = id; }

            public String getTen() { return ten; }
            public void setTen(String ten) { this.ten = ten; }

            public Integer getCapDo() { return capDo; }
            public void setCapDo(Integer capDo) { this.capDo = capDo; }
        }

        public static class ChuDeInfo {
            private Integer id;
            private String ten;

            public Integer getId() { return id; }
            public void setId(Integer id) { this.id = id; }

            public String getTen() { return ten; }
            public void setTen(String ten) { this.ten = ten; }
        }

        public static class GiangVienInfo {
            private Long id;
            private String hoTen;
            private String email;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }

            public String getHoTen() { return hoTen; }
            public void setHoTen(String hoTen) { this.hoTen = hoTen; }

            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
        }
    }
}