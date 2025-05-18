package com.example.app_learn_chinese_2025.util;


import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Mật khẩu phải có ít nhất 6 ký tự
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidPhoneNumber(String phone) {
        // Số điện thoại phải có 10-11 chữ số
        return !TextUtils.isEmpty(phone) && phone.matches("\\d{10,11}");
    }

    public static boolean isValidUsername(String username) {
        // Tên đăng nhập phải có ít nhất 4 ký tự, không chứa khoảng trắng
        return !TextUtils.isEmpty(username) && username.length() >= 4 && !username.contains(" ");
    }
}