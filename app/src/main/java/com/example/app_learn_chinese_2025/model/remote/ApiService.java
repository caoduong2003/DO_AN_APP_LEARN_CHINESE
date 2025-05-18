package com.example.app_learn_chinese_2025.model.remote;

import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.LoginRequest;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/dangnhap")
    Call<JwtResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/dangky")
    Call<User> register(@Body RegisterRequest registerRequest);

    @GET("api/auth/profile")
    Call<User> getUserProfile(@Header("Authorization") String token);
}