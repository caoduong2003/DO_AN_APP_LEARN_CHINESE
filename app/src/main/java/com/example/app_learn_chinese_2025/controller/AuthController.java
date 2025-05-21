package com.example.app_learn_chinese_2025.controller;

import android.content.Context;

import com.example.app_learn_chinese_2025.model.data.RegisterRequest;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.UserRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.util.ValidationUtils;

public class AuthController {
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private Context context;

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public AuthController(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.userRepository = new UserRepository(sessionManager);
    }

    public void login(String username, String password, AuthCallback callback) {
        // Kiểm tra đầu vào
        if (!validateLoginInput(username, password, callback)) {
            return;
        }

        // Gọi repository để đăng nhập
        userRepository.login(username, password, new UserRepository.OnUserResponseCallback() {
            @Override
            public void onSuccess(User user, String token) {
                callback.onSuccess("Đăng nhập thành công");
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void register(String username, String email, String password, String confirmPassword,
                         String fullName, String phone, int role, AuthCallback callback) {
        // Kiểm tra đầu vào
        if (!validateRegisterInput(username, email, password, confirmPassword, fullName, phone, callback)) {
            return;
        }

        // Tạo đối tượng request
        RegisterRequest request = new RegisterRequest();
        request.setTenDangNhap(username);
        request.setEmail(email);
        request.setMatKhau(password);
        request.setHoTen(fullName);
        request.setSoDienThoai(phone);
        request.setVaiTro(role); // Thiết lập vai trò từ tham số
        request.setTrinhDoHSK(0); // Mặc định trình độ HSK = 0

        // Gọi repository để đăng ký
        userRepository.register(request, new UserRepository.OnUserResponseCallback() {
            @Override
            public void onSuccess(User user, String token) {
                callback.onSuccess("Đăng ký thành công");
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void registerWithRole(String username, String email, String password, String confirmPassword,
                                 String fullName, String phone, int role, AuthCallback callback) {
        // Kiểm tra đầu vào
        if (!validateRegisterInput(username, email, password, confirmPassword, fullName, phone, callback)) {
            return;
        }

        // Tạo đối tượng request
        RegisterRequest request = new RegisterRequest();
        request.setTenDangNhap(username);
        request.setEmail(email);
        request.setMatKhau(password);
        request.setHoTen(fullName);
        request.setSoDienThoai(phone);
        request.setVaiTro(role); // Vai trò do người dùng chọn
        request.setTrinhDoHSK(0); // Mặc định là trình độ 0

        // Gọi repository để đăng ký
        userRepository.register(request, new UserRepository.OnUserResponseCallback() {
            @Override
            public void onSuccess(User user, String token) {
                callback.onSuccess("Đăng ký thành công");
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
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

    // Validation methods
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