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
        RetrofitClient.getInstance(sessionManager).getApiService().getTuVungByBaiGiang(baiGiangId)
                .enqueue(new Callback<List<TuVung>>() {
                    @Override
                    public void onResponse(Call<List<TuVung>> call, Response<List<TuVung>> response) {
                        Log.d(TAG, "getTuVungByBaiGiang URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tải danh sách từ vựng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TuVung>> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void getTuVungById(long id, OnTuVungCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().getTuVungById(id)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        Log.d(TAG, "getTuVungById URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tải thông tin từ vựng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void createTuVung(TuVung tuVung, OnTuVungCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().createTuVung(getToken(), tuVung)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        Log.d(TAG, "createTuVung URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tạo từ vựng mới: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void updateTuVung(long id, TuVung tuVung, OnTuVungCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().updateTuVung(getToken(), id, tuVung)
                .enqueue(new Callback<TuVung>() {
                    @Override
                    public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                        Log.d(TAG, "updateTuVung URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể cập nhật từ vựng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<TuVung> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void deleteTuVung(long id, Callback<Void> callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().deleteTuVung(getToken(), id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "deleteTuVung URL: " + call.request().url());
                        if (response.isSuccessful()) {
                            callback.onResponse(call, response);
                        } else {
                            String errorMessage = "Không thể xóa từ vựng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onResponse(call, Response.error(response.code(), response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onFailure(call, t);
                    }
                });
    }

    public void searchTuVung(String keyword, String language, OnTuVungListCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().searchTuVung(keyword, language)
                .enqueue(new Callback<List<TuVung>>() {
                    @Override
                    public void onResponse(Call<List<TuVung>> call, Response<List<TuVung>> response) {
                        Log.d(TAG, "searchTuVung URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tìm kiếm từ vựng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TuVung>> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void generatePinyin(String chineseText, OnStringCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().generatePinyin(getToken(), chineseText)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "generatePinyin URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tạo phiên âm: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void generateAudio(String chineseText, OnStringCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().generateAudio(getToken(), chineseText)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "generateAudio URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "Không thể tạo audio: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void translateVietnameseToChinese(String text, OnTranslationCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().translateVietnameseToChinese(text)
                .enqueue(new Callback<TranslationResponse>() {
                    @Override
                    public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                        Log.d(TAG, "translateVietnameseToChinese URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getTranslatedText());
                        } else {
                            String errorMessage = "Không thể dịch văn bản: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<TranslationResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void translateChineseToVietnamese(String text, OnTranslationCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().translateChineseToVietnamese(text)
                .enqueue(new Callback<TranslationResponse>() {
                    @Override
                    public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                        Log.d(TAG, "translateChineseToVietnamese URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getTranslatedText());
                        } else {
                            String errorMessage = "Không thể dịch văn bản: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<TranslationResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }
}