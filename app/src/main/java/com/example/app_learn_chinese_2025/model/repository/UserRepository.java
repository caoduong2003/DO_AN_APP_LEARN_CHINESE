package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;
import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.LoginRequest;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private SessionManager sessionManager;
    private static final String TAG = "UserRepository";

    public interface OnUserResponseCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    public UserRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void login(String username, String password, OnUserResponseCallback callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Log.d(TAG, "Sending login request: " + new Gson().toJson(loginRequest));

        RetrofitClient.getInstance().getApiService().login(loginRequest)
                .enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        Log.d(TAG, "Login response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            JwtResponse jwtResponse = response.body();
                            Log.d(TAG, "Login response: " + new Gson().toJson(jwtResponse));
                            User user = new User();
                            user.setID(jwtResponse.getId());
                            user.setTenDangNhap(jwtResponse.getTenDangNhap());
                            user.setEmail(jwtResponse.getEmail());
                            user.setHoTen(jwtResponse.getHoTen());
                            user.setVaiTro(jwtResponse.getVaiTro());
                            user.setTrangThai(true); // Giả sử đăng nhập thành công là true
                            sessionManager.createSession(user, jwtResponse.getToken());
                            callback.onSuccess(user, jwtResponse.getToken());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Login failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Đăng nhập thất bại: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                                callback.onError("Đăng nhập thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        Log.e(TAG, "Login network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void register(RegisterRequest request, OnUserResponseCallback callback) {
        Log.d(TAG, "Sending register request: " + new Gson().toJson(request));
        RetrofitClient.getInstance().getApiService().register(request)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Register response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            Log.d(TAG, "Register response: " + new Gson().toJson(user));
                            callback.onSuccess(user, null); // Không có token khi đăng ký
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Register failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Đăng ký thất bại: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                                callback.onError("Đăng ký thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Register network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void getUserProfile(OnUserResponseCallback callback) {
        if (!sessionManager.isLoggedIn()) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String token = "Bearer " + sessionManager.getToken();
        RetrofitClient.getInstance().getApiService().getUserProfile(token)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body(), null);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                callback.onError("Lấy thông tin thất bại: " + errorBody);
                            } catch (Exception e) {
                                callback.onError("Lấy thông tin thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}