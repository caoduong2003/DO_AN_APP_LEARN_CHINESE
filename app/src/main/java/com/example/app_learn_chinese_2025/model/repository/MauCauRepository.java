package com.example.app_learn_chinese_2025.model.repository;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MauCauRepository {
    private static final String TAG = "MauCauRepository";

    public interface OnMauCauListCallback {
        void onSuccess(List<MauCau> mauCauList);
        void onError(String errorMessage);
    }

    public MauCauRepository(Context context, SessionManager sessionManager) {
    }

    public void getMauCauByBaiGiang(long baiGiangId, OnMauCauListCallback callback) {
        RetrofitClient.getInstanceWithoutToken().getApiService().getMauCauByBaiGiang(baiGiangId)
                .enqueue(new Callback<List<MauCau>>() {
                    @Override
                    public void onResponse(Call<List<MauCau>> call, Response<List<MauCau>> response) {
                        Log.d(TAG, "getMauCauByBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tải mẫu câu: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MauCau>> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }
}