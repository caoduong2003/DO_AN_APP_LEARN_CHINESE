package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;
import com.example.app_learn_chinese_2025.api.BaiGiangApiService;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.utils.SessionManager;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaiGiangController {
    private static final String TAG = "BaiGiangController";
    private static final String BASE_URL = "http://1.54.173.124:8080/api/baigiang"; // Thay thế bằng URL thực tế của bạn
    
    private final BaiGiangApiService apiService;
    private final SessionManager sessionManager;
    private OnBaiGiangListener listener;

    public interface OnBaiGiangListener {
        void onBaiGiangListReceived(List<BaiGiang> baiGiangList);
        void onBaiGiangDetailReceived(BaiGiang baiGiang);
        void onError(String message);
    }

    public BaiGiangController(Context context, OnBaiGiangListener listener) {
        this.listener = listener;
        this.sessionManager = new SessionManager(context);
        
        // Tạo OkHttpClient với interceptor để log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Khởi tạo API Service
        this.apiService = retrofit.create(BaiGiangApiService.class);
    }

    public void getBaiGiangList() {
        String token = sessionManager.getToken();
        if (token == null) {
            if (listener != null) {
                listener.onError("Token không tồn tại");
            }
            return;
        }

        apiService.getBaiGiangList("Bearer " + token).enqueue(new Callback<List<BaiGiang>>() {
            @Override
            public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onBaiGiangListReceived(response.body());
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Lỗi: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Lỗi kết nối: " + t.getMessage());
                }
                Log.e(TAG, "Error fetching bai giang list", t);
            }
        });
    }

    public void getBaiGiangDetail(Long id) {
        String token = sessionManager.getToken();
        if (token == null) {
            if (listener != null) {
                listener.onError("Token không tồn tại");
            }
            return;
        }

        apiService.getBaiGiangDetail(id, "Bearer " + token).enqueue(new Callback<BaiGiang>() {
            @Override
            public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) {
                        listener.onBaiGiangDetailReceived(response.body());
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Lỗi: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaiGiang> call, Throwable t) {
                if (listener != null) {
                    listener.onError("Lỗi kết nối: " + t.getMessage());
                }
                Log.e(TAG, "Error fetching bai giang detail", t);
            }
        });
    }
} 