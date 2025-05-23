package com.example.app_learn_chinese_2025.model.repository;

import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MauCauRepository {
    private static final String TAG = "MauCauRepository";
    private SessionManager sessionManager;

    public interface OnMauCauCallback {
        void onSuccess(MauCau mauCau);
        void onError(String errorMessage);
    }

    public interface OnMauCauListCallback {
        void onSuccess(List<MauCau> mauCauList);
        void onError(String errorMessage);
    }

    public MauCauRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        return "Bearer " + sessionManager.getToken();
    }

    public void getMauCauByBaiGiang(long baiGiangId, OnMauCauListCallback callback) {
        RetrofitClient.getInstance().getApiService().getMauCauByBaiGiang(baiGiangId)
                .enqueue(new Callback<List<MauCau>>() {
                    @Override
                    public void onResponse(Call<List<MauCau>> call, Response<List<MauCau>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách mẫu câu");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MauCau>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void getMauCauById(long id, OnMauCauCallback callback) {
        RetrofitClient.getInstance().getApiService().getMauCauById(id)
                .enqueue(new Callback<MauCau>() {
                    @Override
                    public void onResponse(Call<MauCau> call, Response<MauCau> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải thông tin mẫu câu");
                        }
                    }

                    @Override
                    public void onFailure(Call<MauCau> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void createMauCau(MauCau mauCau, OnMauCauCallback callback) {
        RetrofitClient.getInstance().getApiService().createMauCau(getToken(), mauCau)
                .enqueue(new Callback<MauCau>() {
                    @Override
                    public void onResponse(Call<MauCau> call, Response<MauCau> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tạo mẫu câu mới");
                        }
                    }

                    @Override
                    public void onFailure(Call<MauCau> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void updateMauCau(long id, MauCau mauCau, OnMauCauCallback callback) {
        RetrofitClient.getInstance().getApiService().updateMauCau(getToken(), id, mauCau)
                .enqueue(new Callback<MauCau>() {
                    @Override
                    public void onResponse(Call<MauCau> call, Response<MauCau> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể cập nhật mẫu câu");
                        }
                    }

                    @Override
                    public void onFailure(Call<MauCau> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void deleteMauCau(long id, Callback<Void> callback) {
        RetrofitClient.getInstance().getApiService().deleteMauCau(getToken(), id)
                .enqueue(callback);
    }
}