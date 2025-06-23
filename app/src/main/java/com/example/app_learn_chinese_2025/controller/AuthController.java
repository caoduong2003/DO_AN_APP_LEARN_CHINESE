package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.JwtResponse;
import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.util.ValidationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthController {
    private static final String TAG = "AuthController";
    private ApiService apiService;
    private SessionManager sessionManager;
    private Context context;

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public AuthController(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.apiService = RetrofitClient.getInstance(sessionManager).getApiService();
    }

    public void login(String username, String password, AuthCallback callback) {
        // Kiểm tra đầu vào
        if (!validateLoginInput(username, password, callback)) {
            return;
        }

        // Tạo request với constructor mới
        RegisterRequest request = new RegisterRequest(username, password);

        apiService.login(request).enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                Log.d(TAG, "Login response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    User user = jwtResponse.getUser();
                    String token = jwtResponse.getToken();

                    if (user == null) {
                        Log.e(TAG, "User object is null");
                        callback.onError("Lỗi: Không tìm thấy thông tin người dùng");
                        return;
                    }

                    if (user.getVaiTro() != 0) {
                        Log.e(TAG, "User is not admin: " + user.getTenDangNhap());
                        callback.onError("Chỉ admin có quyền đăng nhập vào chức năng quản lý");
                    } else {
                        sessionManager.createSession(user, token);
                        Log.d(TAG, "Login success: " + user.getTenDangNhap());
                        callback.onSuccess("Đăng nhập thành công");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Login failed: HTTP " + response.code() + ", Error: " + errorBody);
                        callback.onError("Đăng nhập thất bại: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        callback.onError("Đăng nhập thất bại: Lỗi xử lý phản hồi");
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

    public void register(String username, String email, String password, String confirmPassword,
                         String fullName, String phone, int role, AuthCallback callback) {
        // Kiểm tra đầu vào
        if (!validateRegisterInput(username, email, password, confirmPassword, fullName, phone, callback)) {
            return;
        }

        RegisterRequest request = new RegisterRequest(username, email, password, fullName, phone);
        request.setVaiTro(role);
        request.setTrinhDoHSK(0);

        apiService.register(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Register response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Register success: " + response.body().getTenDangNhap());
                    callback.onSuccess("Đăng ký thành công");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Register failed: HTTP " + response.code() + ", Error: " + errorBody);
                        callback.onError("Đăng ký thất bại: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        callback.onError("Đăng ký thất bại: Lỗi xử lý phản hồi");
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

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return sessionManager.getUserDetails();
    }

    public void logout() {
        sessionManager.logout();
    }

    public int getUserRole() {
        return sessionManager.getUserRole();
    }

    private boolean validateLoginInput(String username, String password, AuthCallback callback) {
        if (username.isEmpty()) {
            callback.onError("Vui lòng nhập tên đăng nhập");
            return false;
        }

        if (password.isEmpty()) {
            callback.onError("Vui lòng nhập mật khẩu");
            return false;
        }

        return true;
    }

    private boolean validateRegisterInput(String username, String email, String password,
                                          String confirmPassword, String fullName, String phone,
                                          AuthCallback callback) {
        if (!ValidationUtils.isValidUsername(username)) {
            callback.onError("Tên đăng nhập phải có ít nhất 4 ký tự và không chứa khoảng trắng");
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            callback.onError("Email không hợp lệ");
            return false;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            callback.onError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            callback.onError("Mật khẩu nhập lại không khớp");
            return false;
        }

        if (fullName.isEmpty()) {
            callback.onError("Vui lòng nhập họ tên");
            return false;
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            callback.onError("Số điện thoại không hợp lệ");
            return false;
        }

        return true;
    }
}