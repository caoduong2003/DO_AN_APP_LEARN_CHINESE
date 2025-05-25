package com.example.app_learn_chinese_2025.model.remote;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.LoginRequest;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.TranslationResponse;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.model.data.TienTrinh;


import java.util.List;

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
    Call<JwtResponse> login(@Body LoginRequest loginRequest);

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

    // Update this method in ApiService.java
    @GET("api/baigiang")
    Call<List<BaiGiang>> getAllBaiGiang(@Query("giangVienId") Long giangVienId,
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

    @GET("api/baigiang/search")
    Call<List<BaiGiang>> searchBaiGiang(@Query("keyword") String keyword);

    // TuVung APIs
    @GET("api/tuvung/baigiang/{baiGiangId}")
    Call<List<TuVung>> getTuVungByBaiGiang(@Path("baiGiangId") long baiGiangId);

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

    // File upload API
    @Multipart
    @POST("api/files/upload")
    Call<ResponseBody> uploadFile(@Header("Authorization") String token, @Part MultipartBody.Part file);

    // Updated ApiService.java để khớp với database schema
    // 1. Cập nhật method getTuVungById (đã có trong API)
    @GET("api/tuvung/{id}")
    Call<TuVung> getTuVungById(@Path("id") long id);

    // 2. Thêm các API endpoints cho TienTrinhHocTap
    @GET("api/tientrinh/user/{userId}")
    Call<List<TienTrinh>> getTienTrinhByUser(@Header("Authorization") String token, @Path("userId") long userId);

    @GET("api/tientrinh/user/{userId}/baigiang/{baiGiangId}")
    Call<TienTrinh> getTienTrinhByUserAndBaiGiang(@Header("Authorization") String token, @Path("userId") long userId, @Path("baiGiangId") long baiGiangId);

    @POST("api/tientrinh")
    Call<TienTrinh> createTienTrinh(@Header("Authorization") String token, @Body TienTrinh tienTrinh);

    @PUT("api/tientrinh/{id}")
    Call<TienTrinh> updateTienTrinh(@Header("Authorization") String token, @Path("id") long id, @Body TienTrinh tienTrinh);

    @PUT("api/tientrinh/{id}/complete")
    Call<TienTrinh> markTienTrinhCompleted(@Header("Authorization") String token, @Path("id") long id);

    // 3. API cho MauCau
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
    @GET("api/admin/users/role/{role}")
    Call<List<User>> getUsersByRole(@Header("Authorization") String token, @Path("role") int role);

    @GET("api/admin/users/{id}")
    Call<User> getUserById(@Header("Authorization") String token, @Path("id") long id);

    @POST("api/admin/users")
    Call<User> createUser(@Header("Authorization") String token, @Body User user);

    @PUT("api/admin/users/{id}")
    Call<User> updateUser(@Header("Authorization") String token, @Path("id") long id, @Body User user);

    @DELETE("api/admin/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") long id);

    @PUT("api/admin/users/{id}/toggle-status")
    Call<User> toggleUserStatus(@Header("Authorization") String token, @Path("id") long id);
}