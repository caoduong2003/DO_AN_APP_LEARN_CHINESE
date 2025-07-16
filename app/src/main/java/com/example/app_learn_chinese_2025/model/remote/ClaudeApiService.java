package com.example.app_learn_chinese_2025.model.remote;

import com.example.app_learn_chinese_2025.model.data.ClaudeRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ClaudeApiService {
    @POST("v1/messages")
    Call<Map<String, Object>> analyzeImage(
            @Header("x-api-key") String apiKey,
            @Header("anthropic-version") String version,
            @Header("content-type") String contentType,
            @Body ClaudeRequest request
    );
}