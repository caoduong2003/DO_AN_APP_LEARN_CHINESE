package com.example.app_learn_chinese_2025.model.repository;

import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TienTrinhRepository {
    private static final String TAG = "TienTrinhRepository";
    private SessionManager sessionManager;

    public interface OnTienTrinhCallback {
        void onSuccess(TienTrinh tienTrinh);
        void onError(String errorMessage);
    }

    public interface OnTienTrinhListCallback {
        void onSuccess(List<TienTrinh> tienTrinhList);
        void onError(String errorMessage);
    }

    public TienTrinhRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        return "Bearer " + sessionManager.getToken();
    }

    // Get all progress records for a specific user
    public void getTienTrinhByUser(long userId, OnTienTrinhListCallback callback) {
        RetrofitClient.getInstance().getApiService().getTienTrinhByUser(getToken(), userId)
                .enqueue(new Callback<List<TienTrinh>>() {
                    @Override
                    public void onResponse(Call<List<TienTrinh>> call, Response<List<TienTrinh>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải dữ liệu tiến trình");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TienTrinh>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Get the progress record for a specific user and lesson
    public void getTienTrinhByUserAndBaiGiang(long userId, long baiGiangId, OnTienTrinhCallback callback) {
        RetrofitClient.getInstance().getApiService().getTienTrinhByUserAndBaiGiang(getToken(), userId, baiGiangId)
                .enqueue(new Callback<TienTrinh>() {
                    @Override
                    public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else if (response.code() == 404) {
                            // No progress record found, create a new one
                            TienTrinh newTienTrinh = new TienTrinh();
                            newTienTrinh.setTienDo(0);
                            newTienTrinh.setDaHoanThanh(false);
                            callback.onSuccess(newTienTrinh);
                        } else {
                            callback.onError("Không thể tải dữ liệu tiến trình");
                        }
                    }

                    @Override
                    public void onFailure(Call<TienTrinh> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Create or update a progress record
    public void saveTienTrinh(TienTrinh tienTrinh, OnTienTrinhCallback callback) {
        if (tienTrinh.getId() > 0) {
            // Update existing
            RetrofitClient.getInstance().getApiService().updateTienTrinh(getToken(), tienTrinh.getId(), tienTrinh)
                    .enqueue(new Callback<TienTrinh>() {
                        @Override
                        public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Không thể cập nhật tiến trình");
                            }
                        }

                        @Override
                        public void onFailure(Call<TienTrinh> call, Throwable t) {
                            callback.onError("Lỗi kết nối: " + t.getMessage());
                        }
                    });
        } else {
            // Create new
            RetrofitClient.getInstance().getApiService().createTienTrinh(getToken(), tienTrinh)
                    .enqueue(new Callback<TienTrinh>() {
                        @Override
                        public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Không thể tạo tiến trình");
                            }
                        }

                        @Override
                        public void onFailure(Call<TienTrinh> call, Throwable t) {
                            callback.onError("Lỗi kết nối: " + t.getMessage());
                        }
                    });
        }
    }

    // Mark a lesson as completed
    public void markAsCompleted(long tienTrinhId, OnTienTrinhCallback callback) {
        RetrofitClient.getInstance().getApiService().markTienTrinhCompleted(getToken(), tienTrinhId)
                .enqueue(new Callback<TienTrinh>() {
                    @Override
                    public void onResponse(Call<TienTrinh> call, Response<TienTrinh> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể đánh dấu hoàn thành");
                        }
                    }

                    @Override
                    public void onFailure(Call<TienTrinh> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}