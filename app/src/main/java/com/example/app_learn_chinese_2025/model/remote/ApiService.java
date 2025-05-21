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

    // BaiGiang APIs
    @GET("api/baigiang")
    Call<List<BaiGiang>> getAllBaiGiang(@Query("giangVienId") Long giangVienId,
                                        @Query("loaiBaiGiangId") Integer loaiBaiGiangId,
                                        @Query("capDoHSK_ID") Integer capDoHSK_ID,
                                        @Query("chuDeId") Integer chuDeId);

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
}