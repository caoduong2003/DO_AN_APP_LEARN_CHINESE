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
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Auth APIs
    @POST("api/auth/dangnhap")
    Call<JwtResponse> login(@Body RegisterRequest loginRequest);

    @POST("api/auth/dangky")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("api/auth/profile")
    Call<User> getUserProfile(@Header("Authorization") String token);

    // CapDoHSK APIs
    @GET("api/capdohsk")
    Call<List<CapDoHSK>> getAllCapDoHSK();

    // ChuDe APIs
    @GET("api/chude")
    Call<List<ChuDe>> getAllChuDe();

    // LoaiBaiGiang APIs
    @GET("api/loaibaigiang")
    Call<List<LoaiBaiGiang>> getAllLoaiBaiGiang();

    // BaiGiang APIs
    @GET("api/baigiang")
    Call<List<BaiGiang>> getAllBaiGiang(
            @Query("giangVienId") Long giangVienId,
            @Query("loaiBaiGiangId") Integer loaiBaiGiangId,
            @Query("capDoHSK_ID") Integer capDoHSK_ID,
            @Query("chuDeId") Integer chuDeId,
            @Query("published") Boolean published);

    @GET("api/baigiang/{id}")
    Call<BaiGiang> getBaiGiangById(@Path("id") long id);

    @POST("api/baigiang")
    Call<BaiGiang> createBaiGiang(@Header("Authorization") String token, @Body BaiGiang baiGiang);

    @PUT("api/baigiang/{id}")
    Call<BaiGiang> updateBaiGiang(@Header("Authorization") String token, @Path("id") long id, @Body BaiGiang baiGiang);

    @DELETE("api/baigiang/{id}")
    Call<Void> deleteBaiGiang(@Header("Authorization") String token, @Path("id") long id);

    @DELETE("api/baigiang/admin/{id}")
    Call<Void> deleteBaiGiangByAdmin(@Header("Authorization") String token, @Path("id") long id);

    @GET("api/baigiang/search")
    Call<List<BaiGiang>> searchBaiGiang(@Query("keyword") String keyword);

    // TuVung APIs
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

    // Translation APIs
    @POST("api/translation/vi-to-zh")
    Call<TranslationResponse> translateVietnameseToChinese(@Body String text);

    @POST("api/translation/zh-to-vi")
    Call<TranslationResponse> translateChineseToVietnamese(@Body String text);

    // MauCau APIs
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

    // User Management APIs (Admin only)
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

    // File upload APIs
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

    // DTO Classes
    public static class CreateUserRequest {
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

    public static class UpdateUserRequest {
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

    public static class UserResponse {
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

    public static class UserStatistics {
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

    public static class PageResponse<T> {
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
}