package com.example.app_learn_chinese_2025.model.repository;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiGiangRepository {
    private static final String TAG = "BaiGiangRepository";
    private final Context context;

    public interface OnBaiGiangListCallback {
        void onSuccess(List<BaiGiang> baiGiangList);
        void onError(String errorMessage);
    }

    public interface OnBaiGiangCallback {
        void onSuccess(BaiGiang baiGiang);
        void onError(String errorMessage);
    }

    public BaiGiangRepository(Context context, SessionManager sessionManager) {
        this.context = context;
    }

    public void getAllBaiGiang(Long giangVienId, Integer loaiBaiGiangId, Integer capDoHSK_ID, Integer chuDeId, Boolean published, OnBaiGiangListCallback callback) {
        RetrofitClient.getInstanceWithoutToken().getApiService().getAllBaiGiang(giangVienId, loaiBaiGiangId, capDoHSK_ID, chuDeId, published)
                .enqueue(new Callback<List<BaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                        Log.d(TAG, "getAllBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tải danh sách bài giảng: HTTP " + response.code();
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
                    public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void getBaiGiangById(long id, OnBaiGiangCallback callback) {
        RetrofitClient.getInstanceWithoutToken().getApiService().getBaiGiangById(id)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        Log.d(TAG, "getBaiGiangById URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tải bài giảng: HTTP " + response.code();
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
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void createBaiGiang(BaiGiang baiGiang, SessionManager sessionManager, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().createBaiGiang("Bearer " + sessionManager.getToken(), baiGiang)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        Log.d(TAG, "createBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tạo bài giảng: HTTP " + response.code();
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
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void updateBaiGiang(long id, BaiGiang baiGiang, SessionManager sessionManager, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().updateBaiGiang("Bearer " + sessionManager.getToken(), id, baiGiang)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        Log.d(TAG, "updateBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể cập nhật bài giảng: HTTP " + response.code();
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
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void deleteBaiGiang(long id, SessionManager sessionManager, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().deleteBaiGiang("Bearer " + sessionManager.getToken(), id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "deleteBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            String errorMessage = "Không thể xóa bài giảng: HTTP " + response.code();
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
                    public void onFailure(Call<Void> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }
}