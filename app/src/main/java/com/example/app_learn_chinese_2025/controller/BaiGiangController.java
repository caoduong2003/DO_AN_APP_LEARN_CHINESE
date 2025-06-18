package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiGiangController {
    private static final String TAG = "BaiGiangController";

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
    }

    public void getBaiGiangList() {
        Log.d(TAG, "Getting BaiGiang list...");

        // Sử dụng RetrofitClient thống nhất
        RetrofitClient.getInstance().getApiService().getAllBaiGiang(null, null, null, null, null)
                .enqueue(new Callback<List<BaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                        Log.d(TAG, "Response code: " + response.code());

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.d(TAG, "Received " + response.body().size() + " items");
                                if (listener != null) {
                                    listener.onBaiGiangListReceived(response.body());
                                }
                            } else {
                                Log.w(TAG, "Response body is null");
                                if (listener != null) {
                                    listener.onError("Không có dữ liệu");
                                }
                            }
                        } else {
                            String errorMessage = getErrorMessage(response);
                            Log.e(TAG, "Error response: " + errorMessage);
                            if (listener != null) {
                                listener.onError("Lỗi tải dữ liệu: " + errorMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        String errorMessage = getFailureMessage(t);
                        if (listener != null) {
                            listener.onError(errorMessage);
                        }
                    }
                });
    }

    public void getBaiGiangDetail(long id) {
        Log.d(TAG, "Getting BaiGiang detail for ID: " + id);

        RetrofitClient.getInstance().getApiService().getBaiGiangById(id)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        Log.d(TAG, "Detail response code: " + response.code());

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.d(TAG, "Received BaiGiang: " + response.body().getTieuDe());
                                if (listener != null) {
                                    listener.onBaiGiangDetailReceived(response.body());
                                }
                            } else {
                                Log.w(TAG, "Response body is null");
                                if (listener != null) {
                                    listener.onError("Không tìm thấy bài giảng");
                                }
                            }
                        } else {
                            String errorMessage = getErrorMessage(response);
                            Log.e(TAG, "Error response: " + errorMessage);
                            if (listener != null) {
                                listener.onError("Lỗi tải bài giảng: " + errorMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        String errorMessage = getFailureMessage(t);
                        if (listener != null) {
                            listener.onError(errorMessage);
                        }
                    }
                });
    }

    // Helper method để parse error response
    private String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBodyString = response.errorBody().string();
                Log.d(TAG, "Error body: " + errorBodyString);

                // Try to parse JSON error response
                try {
                    Gson gson = new Gson();
                    ErrorResponse errorResponse = gson.fromJson(errorBodyString, ErrorResponse.class);
                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        return errorResponse.getMessage();
                    }
                } catch (JsonSyntaxException e) {
                    Log.w(TAG, "Could not parse error as JSON: " + e.getMessage());
                }

                return errorBodyString;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body: " + e.getMessage());
        }

        return "HTTP " + response.code() + " - " + response.message();
    }

    // Helper method để parse failure message
    private String getFailureMessage(Throwable t) {
        if (t instanceof java.net.UnknownHostException) {
            return "Không thể kết nối đến server. Kiểm tra kết nối mạng.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            return "Kết nối timeout. Thử lại sau.";
        } else if (t instanceof java.io.IOException) {
            return "Lỗi kết nối mạng: " + t.getMessage();
        } else if (t instanceof JsonSyntaxException) {
            return "Lỗi định dạng dữ liệu từ server.";
        } else {
            return "Lỗi không xác định: " + t.getMessage();
        }
    }

    // Inner class for error response parsing
    private static class ErrorResponse {
        private String message;
        private String error;
        private int status;

        public String getMessage() {
            return message != null ? message : error;
        }
    }

    // Method để debug response raw
    public void debugGetBaiGiangList() {
        Log.d(TAG, "=== DEBUG: Getting BaiGiang list ===");

        RetrofitClient.getInstance().getApiService().getAllBaiGiang(null, null, null, null, null)
                .enqueue(new Callback<List<BaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                        Log.d(TAG, "=== DEBUG RESPONSE ===");
                        Log.d(TAG, "URL: " + call.request().url());
                        Log.d(TAG, "Response code: " + response.code());
                        Log.d(TAG, "Response message: " + response.message());
                        Log.d(TAG, "Response headers: " + response.headers());

                        if (response.body() != null) {
                            Log.d(TAG, "Response body type: " + response.body().getClass().getName());
                            Log.d(TAG, "Response body size: " + response.body().size());

                            // Log first item if available
                            if (!response.body().isEmpty()) {
                                BaiGiang firstItem = response.body().get(0);
                                Log.d(TAG, "First item: " + new Gson().toJson(firstItem));
                            }
                        } else {
                            Log.d(TAG, "Response body is null");
                        }

                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.d(TAG, "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body: " + e.getMessage());
                            }
                        }

                        Log.d(TAG, "=== END DEBUG RESPONSE ===");

                        // Continue with normal processing
                        if (response.isSuccessful() && response.body() != null) {
                            if (listener != null) {
                                listener.onBaiGiangListReceived(response.body());
                            }
                        } else {
                            if (listener != null) {
                                listener.onError("Debug: Response not successful or body null");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                        Log.e(TAG, "=== DEBUG FAILURE ===");
                        Log.e(TAG, "URL: " + call.request().url());
                        Log.e(TAG, "Error type: " + t.getClass().getName());
                        Log.e(TAG, "Error message: " + t.getMessage());
                        Log.e(TAG, "=== END DEBUG FAILURE ===", t);

                        if (listener != null) {
                            listener.onError("Debug: " + getFailureMessage(t));
                        }
                    }
                });
    }
}