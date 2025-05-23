package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.TranslationResponse;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TuVungRepository {
    private static final String TAG = "TuVungRepository";
    private SessionManager sessionManager;

    public interface OnTuVungCallback {
        void onSuccess(TuVung tuVung);
        void onError(String errorMessage);
    }

    public interface OnTuVungListCallback {
        void onSuccess(List<TuVung> tuVungList);
        void onError(String errorMessage);
    }

    public interface OnStringCallback {
        void onSuccess(String result);
        void onError(String errorMessage);
    }

    public interface OnTranslationCallback {
        void onSuccess(String translatedText);
        void onError(String errorMessage);
    }

    public TuVungRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        return "Bearer " + sessionManager.getToken();
    }

    public void getTuVungByBaiGiang(long baiGiangId, OnTuVungListCallback callback) {
        RetrofitClient.getInstance().getApiService().getTuVungByBaiGiang(baiGiangId)
                .enqueue(new Callback<List<TuVung>>() {
                    @Override
                    public void onResponse(Call<List<TuVung>> call, Response<List<TuVung>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách từ vựng");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TuVung>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void createTuVung(TuVung tuVung, OnTuVungCallback callback) {
        RetrofitClient.getInstance().getApiService().createTuVung(getToken(), tuVung)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tạo từ vựng mới");
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void updateTuVung(long id, TuVung tuVung, OnTuVungCallback callback) {
        RetrofitClient.getInstance().getApiService().updateTuVung(getToken(), id, tuVung)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể cập nhật từ vựng");
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void deleteTuVung(long id, Callback<Void> callback) {
        RetrofitClient.getInstance().getApiService().deleteTuVung(getToken(), id)
                .enqueue(callback);
    }

    public void searchTuVung(String keyword, String language, OnTuVungListCallback callback) {
        RetrofitClient.getInstance().getApiService().searchTuVung(keyword, language)
                .enqueue(new Callback<List<TuVung>>() {
                    @Override
                    public void onResponse(Call<List<TuVung>> call, Response<List<TuVung>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tìm kiếm từ vựng");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TuVung>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void generatePinyin(String chineseText, OnStringCallback callback) {
        RetrofitClient.getInstance().getApiService().generatePinyin(getToken(), chineseText)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tạo phiên âm");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void generateAudio(String chineseText, OnStringCallback callback) {
        RetrofitClient.getInstance().getApiService().generateAudio(getToken(), chineseText)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tạo audio");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void translateVietnameseToChinese(String text, OnTranslationCallback callback) {
        RetrofitClient.getInstance().getApiService().translateVietnameseToChinese(text)
                .enqueue(new Callback<TranslationResponse>() {
                    @Override
                    public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getTranslatedText());
                        } else {
                            callback.onError("Không thể dịch văn bản");
                        }
                    }

                    @Override
                    public void onFailure(Call<TranslationResponse> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void translateChineseToVietnamese(String text, OnTranslationCallback callback) {
        RetrofitClient.getInstance().getApiService().translateChineseToVietnamese(text)
                .enqueue(new Callback<TranslationResponse>() {
                    @Override
                    public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getTranslatedText());
                        } else {
                            callback.onError("Không thể dịch văn bản");
                        }
                    }

                    @Override
                    public void onFailure(Call<TranslationResponse> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
    // Add this method to TuVungRepository.java
    public void getTuVungById(long id, OnTuVungCallback callback) {
        // Send API request to get a specific TuVung by ID
        RetrofitClient.getInstance().getApiService().getTuVungById(id)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải thông tin từ vựng");
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}