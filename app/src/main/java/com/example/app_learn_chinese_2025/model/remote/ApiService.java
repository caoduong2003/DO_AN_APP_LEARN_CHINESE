package com.example.app_learn_chinese_2025.model.remote;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.TranslationResponse;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.data.BaiTap;
import com.example.app_learn_chinese_2025.model.data.KetQuaBaiTap;
import com.example.app_learn_chinese_2025.model.request.LamBaiTapRequest;
import com.example.app_learn_chinese_2025.model.response.ApiResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ===== AUTH APIs =====
    @POST("api/auth/dangnhap")
    Call<JwtResponse> login(@Body RegisterRequest loginRequest);

    @POST("api/auth/dangky")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("api/auth/profile")
    Call<User> getUserProfile(@Header("Authorization") String token);

    // ===== GUEST APIs - Không cần authentication =====
    @GET("api/guest/stats")
    Call<Map<String, Object>> getGuestStats();

    @GET("api/guest/baigiang")
    Call<List<BaiGiang>> getGuestBaiGiang(
            @Query("limit") int limit,
            @Query("capDoHSK_ID") Integer capDoHSK_ID,
            @Query("chuDeId") Integer chuDeId);

    @GET("api/guest/baigiang/{id}")
    Call<BaiGiang> getGuestBaiGiangDetail(@Path("id") long id);

    @GET("api/guest/tuvung/{baiGiangId}")
    Call<List<TuVung>> getGuestTuVung(
            @Path("baiGiangId") long baiGiangId,
            @Query("limit") int limit);

    @GET("api/guest/chude")
    Call<List<ChuDe>> getGuestChuDe();

    @GET("api/guest/capdohsk")
    Call<List<CapDoHSK>> getGuestCapDoHSK();

    @GET("api/guest/loaibaigiang")
    Call<List<LoaiBaiGiang>> getGuestLoaiBaiGiang();

    // ===== GENERAL APIs =====

    // CapDoHSK APIs
    @GET("api/capdohsk")
    Call<List<CapDoHSK>> getAllCapDoHSK();

    // ChuDe APIs
    @GET("api/chude")
    Call<List<ChuDe>> getAllChuDe();

    // LoaiBaiGiang APIs
    @GET("api/loaibaigiang")
    Call<List<LoaiBaiGiang>> getAllLoaiBaiGiang();

    // ===== STUDENT/GENERAL BAIGIANG APIs - Sử dụng model BaiGiang cũ =====

    /**
     * API cho Student và các chức năng general
     * Sử dụng model BaiGiang cũ - KHÔNG THAY ĐỔI
     */
    @GET("api/baigiang")
    Call<List<BaiGiang>> getAllBaiGiang(
            @Query("giangVienId") Long giangVienId,
            @Query("loaiBaiGiangId") Integer loaiBaiGiangId,
            @Query("capDoHSK_ID") Integer capDoHSK_ID,
            @Query("chuDeId") Integer chuDeId,
            @Query("published") Boolean published);

    @GET("api/baigiang/{id}")
    Call<BaiGiang> getBaiGiangById(@Path("id") long id);

    @GET("api/baigiang/search")
    Call<List<BaiGiang>> searchBaiGiang(@Query("keyword") String keyword);

    // ===== DEPRECATED CRUD APIs - Chỉ dành cho Admin hoặc legacy =====

    /**
     * API cũ đã deprecated - chỉ dành cho Admin operations
     * Teacher nên sử dụng /api/teacher-baigiang thay thế
     */
    @Deprecated
    @POST("api/baigiang")
    Call<BaiGiang> createBaiGiang(@Header("Authorization") String token, @Body BaiGiang baiGiang);

    @Deprecated
    @PUT("api/baigiang/{id}")
    Call<BaiGiang> updateBaiGiang(@Header("Authorization") String token, @Path("id") long id, @Body BaiGiang baiGiang);

    @Deprecated
    @DELETE("api/baigiang/{id}")
    Call<Void> deleteBaiGiang(@Header("Authorization") String token, @Path("id") long id);

    // Admin-only operations
    @DELETE("api/baigiang/admin/{id}")
    Call<Void> deleteBaiGiangByAdmin(@Header("Authorization") String token, @Path("id") long id);

    // ===== TEACHER BAIGIANG APIs - API mới chuyên dụng cho Teacher =====

    /**
     * API chuyên dụng cho Teacher CRUD operations
     * Sử dụng TeacherBaiGiangRequest/Response models
     */

    /**
     * Lấy danh sách bài giảng của giáo viên với phân trang
     * GET /api/teacher-baigiang
     */
    @GET("api/teacher-baigiang")
    Call<TeacherBaiGiangResponse.PageResponse> getTeacherBaiGiangs(
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
     * Lấy chi tiết bài giảng của giáo viên
     * GET /api/teacher-baigiang/{id}
     */
    @GET("api/teacher-baigiang/{id}")
    Call<TeacherBaiGiangResponse.DetailResponse> getTeacherBaiGiangDetail(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Tạo bài giảng mới
     * POST /api/teacher-baigiang
     */
    @POST("api/teacher-baigiang")
    Call<TeacherBaiGiangResponse.SimpleResponse> createTeacherBaiGiang(
            @Header("Authorization") String token,
            @Body TeacherBaiGiangRequest.CreateRequest request
    );

    /**
     * Cập nhật bài giảng
     * PUT /api/teacher-baigiang/{id}
     */
    @PUT("api/teacher-baigiang/{id}")
    Call<TeacherBaiGiangResponse.SimpleResponse> updateTeacherBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body TeacherBaiGiangRequest.UpdateRequest request
    );

    /**
     * Xóa bài giảng (soft delete)
     * DELETE /api/teacher-baigiang/{id}
     */
    @DELETE("api/teacher-baigiang/{id}")
    Call<Map<String, String>> deleteTeacherBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Thay đổi trạng thái công khai/ẩn bài giảng
     * PATCH /api/teacher-baigiang/{id}/toggle-status
     */
    @PATCH("api/teacher-baigiang/{id}/toggle-status")
    Call<TeacherBaiGiangResponse.SimpleResponse> toggleTeacherBaiGiangStatus(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Thay đổi trạng thái premium
     * PATCH /api/teacher-baigiang/{id}/toggle-premium
     */
    @PATCH("api/teacher-baigiang/{id}/toggle-premium")
    Call<TeacherBaiGiangResponse.SimpleResponse> toggleTeacherBaiGiangPremium(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    /**
     * Nhân bản bài giảng
     * POST /api/teacher-baigiang/{id}/duplicate
     */
    @POST("api/teacher-baigiang/{id}/duplicate")
    Call<TeacherBaiGiangResponse.SimpleResponse> duplicateTeacherBaiGiang(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body Map<String, String> options
    );

    /**
     * Lấy thống kê bài giảng của giáo viên
     * GET /api/teacher-baigiang/statistics
     */
    @GET("api/teacher-baigiang/statistics")
    Call<TeacherBaiGiangResponse.StatsResponse> getTeacherBaiGiangStatistics(
            @Header("Authorization") String token
    );

    /**
     * Tìm kiếm bài giảng của giáo viên
     * GET /api/teacher-baigiang/search
     */
    @GET("api/teacher-baigiang/search")
    Call<List<TeacherBaiGiangResponse.SimpleResponse>> searchTeacherBaiGiangs(
            @Header("Authorization") String token,
            @Query("keyword") String keyword,
            @Query("limit") int limit
    );

    // ===== TUVUNG APIs =====
    @GET("api/tuvung/baigiang/{baiGiangId}")
    Call<List<TuVung>> getTuVungByBaiGiang(@Path("baiGiangId") long baiGiangId);

    @GET("api/tuvung/{id}")
    Call<TuVung> getTuVungById(@Path("id") long id);

    @POST("api/tuvung")
    Call<TuVung> createTuVung(@Header("Authorization") String token, @Body TuVung tuVung);

    @PUT("api/tuvung/{id}")
    Call<TuVung> updateTuVung(@Header("Authorization") String token, @Path("id") long id, @Body TuVung tuVung);

    @DELETE("api/tuvung/{id}")
    Call<Void> deleteTuVung(@Header("Authorization") String token, @Path("id") long id);

    @GET("api/tuvung/search")
    Call<List<TuVung>> searchTuVung(@Query("keyword") String keyword, @Query("language") String language);

    @POST("api/tuvung/pinyin")
    Call<String> generatePinyin(@Header("Authorization") String token, @Body String chineseText);

    @POST("api/tuvung/audio")
    Call<String> generateAudio(@Header("Authorization") String token, @Body String chineseText);

    // ===== TRANSLATION APIs =====
    @POST("api/translation/vi-to-zh")
    Call<TranslationResponse> translateVietnameseToChinese(@Body String text);

    @POST("api/translation/zh-to-vi")
    Call<TranslationResponse> translateChineseToVietnamese(@Body String text);

    // ===== MAUCAU APIs =====
    @GET("api/maucau/baigiang/{baiGiangId}")
    Call<List<MauCau>> getMauCauByBaiGiang(@Path("baiGiangId") long baiGiangId);

    @GET("api/maucau/{id}")
    Call<MauCau> getMauCauById(@Path("id") long id);

    @POST("api/maucau")
    Call<MauCau> createMauCau(@Header("Authorization") String token, @Body MauCau mauCau);

    @PUT("api/maucau/{id}")
    Call<MauCau> updateMauCau(@Header("Authorization") String token, @Path("id") long id, @Body MauCau mauCau);

    @DELETE("api/maucau/{id}")
    Call<Void> deleteMauCau(@Header("Authorization") String token, @Path("id") long id);

    // ===== USER MANAGEMENT APIs (Admin only) =====
    @GET("api/admin/users")
    Call<PageResponse<UserResponse>> getAllUsers(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir,
            @Query("vaiTro") Integer vaiTro,
            @Query("keyword") String keyword);

    @GET("api/admin/users/teachers")
    Call<List<UserResponse>> getAllTeachers(@Header("Authorization") String token);

    @GET("api/admin/users/students")
    Call<List<UserResponse>> getAllStudents(@Header("Authorization") String token);

    @GET("api/admin/users/{id}")
    Call<UserResponse> getUserById(@Header("Authorization") String token, @Path("id") long id);

    @POST("api/admin/users")
    Call<UserResponse> createUser(@Header("Authorization") String token, @Body CreateUserRequest user);

    @PUT("api/admin/users/{id}")
    Call<UserResponse> updateUser(@Header("Authorization") String token, @Path("id") long id, @Body UpdateUserRequest user);

    @PUT("api/admin/users/{id}/status")
    Call<Void> changeUserStatus(@Header("Authorization") String token, @Path("id") long id, @Query("trangThai") Boolean trangThai);

    @PUT("api/admin/users/{id}/reset-password")
    Call<String> resetUserPassword(@Header("Authorization") String token, @Path("id") long id);

    @DELETE("api/admin/users/{id}")
    Call<String> deleteUser(@Header("Authorization") String token, @Path("id") long id);

    @GET("api/admin/users/statistics")
    Call<UserStatistics> getUserStatistics(@Header("Authorization") String token);

    @GET("api/admin/users/search")
    Call<List<UserResponse>> searchUsers(
            @Header("Authorization") String token,
            @Query("keyword") String keyword,
            @Query("vaiTro") Integer vaiTro);

    // ===== FILE UPLOAD APIs =====
    @Multipart
    @POST("api/files/upload")
    Call<ResponseBody> uploadFile(@Header("Authorization") String token, @Part MultipartBody.Part file);

    @Multipart
    @POST("api/baigiang/{id}/upload-video")
    Call<Map<String, Object>> uploadVideoForLesson(@Header("Authorization") String token, @Path("id") long id, @Part MultipartBody.Part video);

    @Multipart
    @POST("api/baigiang/{id}/upload-thumbnail")
    Call<Map<String, Object>> uploadThumbnailForLesson(@Header("Authorization") String token, @Path("id") long id, @Part MultipartBody.Part image);

    @Multipart
    @POST("api/media/upload/video")
    Call<Map<String, Object>> uploadVideo(@Header("Authorization") String token, @Part MultipartBody.Part video);

    @Multipart
    @POST("api/media/upload/image")
    Call<Map<String, Object>> uploadImage(@Header("Authorization") String token, @Part MultipartBody.Part image);

    // ===== BAITAP APIs =====
    /**
     * Test API bài tập
     * GET /api/bai-tap/ping
     */
    @GET("api/bai-tap/ping")
    Call<ApiResponse<String>> pingBaiTap();

    /**
     * Lấy danh sách bài tập
     * GET /api/bai-tap?capDoHSKId=1&chuDeId=2
     */
    @GET("api/bai-tap")
    Call<ApiResponse<List<BaiTap>>> getBaiTapList(
            @Query("capDoHSKId") Integer capDoHSKId,
            @Query("chuDeId") Integer chuDeId
    );

    /**
     * Lấy chi tiết bài tập
     * GET /api/bai-tap/{id}
     */
    @GET("api/bai-tap/{id}")
    Call<ApiResponse<BaiTap>> getBaiTapDetail(@Path("id") Long id);

    /**
     * Nộp bài tập
     * POST /api/bai-tap/lam-bai
     */
    @POST("api/bai-tap/lam-bai")
    Call<ApiResponse<KetQuaBaiTap>> submitBaiTap(@Body LamBaiTapRequest request);

    /**
     * Lấy kết quả bài tập của học viên
     * GET /api/bai-tap/ket-qua
     */
    @GET("api/bai-tap/ket-qua")
    Call<ApiResponse<List<KetQuaBaiTap>>> getKetQuaBaiTap();

    /**
     * Lấy thống kê bài tập
     * GET /api/bai-tap/thong-ke
     */
    @GET("api/bai-tap/thong-ke")
    Call<ApiResponse<Object>> getThongKeBaiTap();

    // ===== DATA CLASSES =====

    // ===== USER MANAGEMENT DATA CLASSES =====
    class CreateUserRequest {
        private String tenDangNhap;
        private String email;
        private String matKhau;
        private String hoTen;
        private String soDienThoai;
        private Integer vaiTro;
        private Integer trinhDoHSK;
        private String hinhDaiDien;
        private Boolean trangThai = true;

        // Getters and setters
        public String getTenDangNhap() { return tenDangNhap; }
        public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMatKhau() { return matKhau; }
        public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
        public Integer getVaiTro() { return vaiTro; }
        public void setVaiTro(Integer vaiTro) { this.vaiTro = vaiTro; }
        public Integer getTrinhDoHSK() { return trinhDoHSK; }
        public void setTrinhDoHSK(Integer trinhDoHSK) { this.trinhDoHSK = trinhDoHSK; }
        public String getHinhDaiDien() { return hinhDaiDien; }
        public void setHinhDaiDien(String hinhDaiDien) { this.hinhDaiDien = hinhDaiDien; }
        public Boolean getTrangThai() { return trangThai; }
        public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }
    }

    class UpdateUserRequest {
        private String email;
        private String hoTen;
        private String soDienThoai;
        private Integer vaiTro;
        private Integer trinhDoHSK;
        private String hinhDaiDien;
        private Boolean trangThai;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
        public Integer getVaiTro() { return vaiTro; }
        public void setVaiTro(Integer vaiTro) { this.vaiTro = vaiTro; }
        public Integer getTrinhDoHSK() { return trinhDoHSK; }
        public void setTrinhDoHSK(Integer trinhDoHSK) { this.trinhDoHSK = trinhDoHSK; }
        public String getHinhDaiDien() { return hinhDaiDien; }
        public void setHinhDaiDien(String hinhDaiDien) { this.hinhDaiDien = hinhDaiDien; }
        public Boolean getTrangThai() { return trangThai; }
        public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }
    }

    class UserResponse {
        private Long id;
        private String tenDangNhap;
        private String email;
        private String hoTen;
        private String soDienThoai;
        private Integer vaiTro;
        private String vaiTroText;
        private Integer trinhDoHSK;
        private String hinhDaiDien;
        private String ngayTao;
        private String ngayCapNhat;
        private String lanDangNhapCuoi;
        private Boolean trangThai;
        private String trangThaiText;
        private Integer soBaiGiangDaTao;
        private Integer soBaiGiangDaHoc;
        private Integer tienDoHocTap;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTenDangNhap() { return tenDangNhap; }
        public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
        public Integer getVaiTro() { return vaiTro; }
        public void setVaiTro(Integer vaiTro) { this.vaiTro = vaiTro; }
        public String getVaiTroText() { return vaiTroText; }
        public void setVaiTroText(String vaiTroText) { this.vaiTroText = vaiTroText; }
        public Integer getTrinhDoHSK() { return trinhDoHSK; }
        public void setTrinhDoHSK(Integer trinhDoHSK) { this.trinhDoHSK = trinhDoHSK; }
        public String getHinhDaiDien() { return hinhDaiDien; }
        public void setHinhDaiDien(String hinhDaiDien) { this.hinhDaiDien = hinhDaiDien; }
        public String getNgayTao() { return ngayTao; }
        public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }
        public String getNgayCapNhat() { return ngayCapNhat; }
        public void setNgayCapNhat(String ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
        public String getLanDangNhapCuoi() { return lanDangNhapCuoi; }
        public void setLanDangNhapCuoi(String lanDangNhapCuoi) { this.lanDangNhapCuoi = lanDangNhapCuoi; }
        public Boolean getTrangThai() { return trangThai; }
        public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }
        public String getTrangThaiText() { return trangThaiText; }
        public void setTrangThaiText(String trangThaiText) { this.trangThaiText = trangThaiText; }
        public Integer getSoBaiGiangDaTao() { return soBaiGiangDaTao; }
        public void setSoBaiGiangDaTao(Integer soBaiGiangDaTao) { this.soBaiGiangDaTao = soBaiGiangDaTao; }
        public Integer getSoBaiGiangDaHoc() { return soBaiGiangDaHoc; }
        public void setSoBaiGiangDaHoc(Integer soBaiGiangDaHoc) { this.soBaiGiangDaHoc = soBaiGiangDaHoc; }
        public Integer getTienDoHocTap() { return tienDoHocTap; }
        public void setTienDoHocTap(Integer tienDoHocTap) { this.tienDoHocTap = tienDoHocTap; }
    }

    class UserStatistics {
        private Long tongSoNguoiDung;
        private Long soAdmin;
        private Long soGiangVien;
        private Long soHocVien;
        private Long soNguoiDungHoatDong;
        private Long soNguoiDungBiKhoa;
        private Long soNguoiDungMoi7Ngay;
        private Long soNguoiDungMoi30Ngay;
        private Long soNguoiDungDangNhapHomNay;
        private Long soNguoiDungDangNhap7Ngay;

        // Getters and setters
        public Long getTongSoNguoiDung() { return tongSoNguoiDung; }
        public void setTongSoNguoiDung(Long tongSoNguoiDung) { this.tongSoNguoiDung = tongSoNguoiDung; }
        public Long getSoAdmin() { return soAdmin; }
        public void setSoAdmin(Long soAdmin) { this.soAdmin = soAdmin; }
        public Long getSoGiangVien() { return soGiangVien; }
        public void setSoGiangVien(Long soGiangVien) { this.soGiangVien = soGiangVien; }
        public Long getSoHocVien() { return soHocVien; }
        public void setSoHocVien(Long soHocVien) { this.soHocVien = soHocVien; }
        public Long getSoNguoiDungHoatDong() { return soNguoiDungHoatDong; }
        public void setSoNguoiDungHoatDong(Long soNguoiDungHoatDong) { this.soNguoiDungHoatDong = soNguoiDungHoatDong; }
        public Long getSoNguoiDungBiKhoa() { return soNguoiDungBiKhoa; }
        public void setSoNguoiDungBiKhoa(Long soNguoiDungBiKhoa) { this.soNguoiDungBiKhoa = soNguoiDungBiKhoa; }
        public Long getSoNguoiDungMoi7Ngay() { return soNguoiDungMoi7Ngay; }
        public void setSoNguoiDungMoi7Ngay(Long soNguoiDungMoi7Ngay) { this.soNguoiDungMoi7Ngay = soNguoiDungMoi7Ngay; }
        public Long getSoNguoiDungMoi30Ngay() { return soNguoiDungMoi30Ngay; }
        public void setSoNguoiDungMoi30Ngay(Long soNguoiDungMoi30Ngay) { this.soNguoiDungMoi30Ngay = soNguoiDungMoi30Ngay; }
        public Long getSoNguoiDungDangNhapHomNay() { return soNguoiDungDangNhapHomNay; }
        public void setSoNguoiDungDangNhapHomNay(Long soNguoiDungDangNhapHomNay) { this.soNguoiDungDangNhapHomNay = soNguoiDungDangNhapHomNay; }
        public Long getSoNguoiDungDangNhap7Ngay() { return soNguoiDungDangNhap7Ngay; }
        public void setSoNguoiDungDangNhap7Ngay(Long soNguoiDungDangNhap7Ngay) { this.soNguoiDungDangNhap7Ngay = soNguoiDungDangNhap7Ngay; }
    }

    class PageResponse<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int size;
        private int number;
        private boolean first;
        private boolean last;

        // Getters and setters
        public List<T> getContent() { return content; }
        public void setContent(List<T> content) { this.content = content; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public boolean isFirst() { return first; }
        public void setFirst(boolean first) { this.first = first; }
        public boolean isLast() { return last; }
        public void setLast(boolean last) { this.last = last; }
    }

    // ===== TEACHER BAI GIANG DATA CLASSES =====

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

        public static class UpdateRequest {
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

            // Getters and Setters (same as CreateRequest)
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
    }

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
            private Integer tongSoBaiGiang;
            private Integer baiGiangCongKhai;
            private Integer baiGiangGoi;
            private Integer tongLuotXem;
            private Double luotXemTrungBinh;

            // Getters and Setters
            public Integer getTongSoBaiGiang() { return tongSoBaiGiang; }
            public void setTongSoBaiGiang(Integer tongSoBaiGiang) { this.tongSoBaiGiang = tongSoBaiGiang; }

            public Integer getBaiGiangCongKhai() { return baiGiangCongKhai; }
            public void setBaiGiangCongKhai(Integer baiGiangCongKhai) { this.baiGiangCongKhai = baiGiangCongKhai; }

            public Integer getBaiGiangGoi() { return baiGiangGoi; }
            public void setBaiGiangGoi(Integer baiGiangGoi) { this.baiGiangGoi = baiGiangGoi; }

            public Integer getTongLuotXem() { return tongLuotXem; }
            public void setTongLuotXem(Integer tongLuotXem) { this.tongLuotXem = tongLuotXem; }

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