package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final SessionManager sessionManager;

    public interface OnUserResponseCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    public UserRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void login(String username, String password, OnUserResponseCallback callback) {
        RegisterRequest loginRequest = new RegisterRequest();
        loginRequest.setTenDangNhap(username);
        loginRequest.setMatKhau(password);
        Log.d(TAG, "Sending login request: " + new Gson().toJson(loginRequest));

        RetrofitClient.getInstanceWithoutToken().getApiService().login(loginRequest)
                .enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        Log.d(TAG, "Login URL: " + call.request().url());
                        Log.d(TAG, "Login response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            JwtResponse jwtResponse = response.body();
                            User user = new User();
                            user.setID(jwtResponse.getId());
                            user.setTenDangNhap(jwtResponse.getTenDangNhap());
                            user.setEmail(jwtResponse.getEmail());
                            user.setHoTen(jwtResponse.getHoTen());
                            user.setVaiTro(jwtResponse.getVaiTro());
                            user.setTrangThai(true);
                            sessionManager.createSession(user, jwtResponse.getToken());
                            callback.onSuccess(user, jwtResponse.getToken());
                        } else {
                            String errorMessage = "Đăng nhập thất bại: HTTP " + response.code();
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
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void register(RegisterRequest request, OnUserResponseCallback callback) {
        if (request == null) {
            callback.onError("Dữ liệu đăng ký không hợp lệ");
            return;
        }
        if (request.getMatKhau() == null || request.getMatKhau().isEmpty()) {
            callback.onError("Mật khẩu không được để trống");
            return;
        }
        Log.d(TAG, "Sending register request: " + new Gson().toJson(request));

        RetrofitClient.getInstanceWithoutToken().getApiService().register(request)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Register URL: " + call.request().url());
                        Log.d(TAG, "Register response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            callback.onSuccess(user, null);
                        } else {
                            String errorMessage = "Đăng ký thất bại: HTTP " + response.code();
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
                    public void onFailure(Call<User> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void getUserProfile(OnUserResponseCallback callback) {
        if (!sessionManager.isLoggedIn()) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        String token = "Bearer " + sessionManager.getToken();
        RetrofitClient.getInstance(sessionManager).getApiService().getUserProfile(token)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "getUserProfile URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body(), null);
                        } else {
                            String errorMessage = "Lấy thông tin thất bại: HTTP " + response.code();
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
                    public void onFailure(Call<User> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }
}