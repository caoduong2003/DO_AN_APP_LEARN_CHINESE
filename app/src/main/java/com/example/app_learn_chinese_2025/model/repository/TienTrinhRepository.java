package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TienTrinhRepository {
    private static final String TAG = "TienTrinhRepository";

    public interface OnTienTrinhCallback {
        void onSuccess(TienTrinh tienTrinh);
        void onError(String errorMessage);
    }

//    public void getTienTrinhByUserAndBaiGiang(long userId, long baiGiangId, SessionManager sessionManager, OnTienTrinhCallback callback) {
//        RetrofitClient.getInstance(sessionManager).getApiService().getTienTrinhByUserAndBaiGiang(userId, baiGiangId)
//                .enqueue(new Callback<TienTrinh>() {
//                    @Override
//                    public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
//                        Log.d(TAG, "getTienTrinhByUserAndBaiGiang URL: " + call.request().url());
//                        if (response.isSuccessful() && response.body() != null) {
//                            callback.onSuccess(response.body());
//                        } else {
//                            String errorMessage = "Không thể tải tiến trình: HTTP " + response.code();
//                            try {
//                                if (response.errorBody() != null) {
//                                    errorMessage += " - " + response.errorBody().string();
//                                }
//                            } catch (IOException e) {
//                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
//                            }
//                            Log.e(TAG, errorMessage);
//                            callback.onError(errorMessage);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<TienTrinh> call, Throwable t) {
//                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
//                        Log.e(TAG, errorMessage, t);
//                        callback.onError(errorMessage);
//                    }
//                });
//    }

//    public void saveTienTrinh(TienTrinh tienTrinh, SessionManager sessionManager, OnTienTrinhCallback callback) {
//        RetrofitClient.getInstance(sessionManager).getApiService().saveTienTrinh("Bearer " + sessionManager.getToken(), tienTrinh)
//                .enqueue(new Callback<TienTrinh>() {
//                    @Override
//                    public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
//                        Log.d(TAG, "saveTienTrinh URL: " + call.request().url());
//                        if (response.isSuccessful() && response.body() != null) {
//                            callback.onSuccess(response.body());
//                        } else {
//                            String errorMessage = "Không thể lưu tiến trình: HTTP " + response.code();
//                            try {
//                                if (response.errorBody() != null) {
//                                    errorMessage += " - " + response.errorBody().string();
//                                }
//                            } catch (IOException e) {
//                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
//                            }
//                            Log.e(TAG, errorMessage);
//                            callback.onError(errorMessage);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<TienTrinh> call, Throwable t) {
//                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
//                        Log.e(TAG, errorMessage, t);
//                        callback.onError(errorMessage);
//                    }
//                });
//    }
}