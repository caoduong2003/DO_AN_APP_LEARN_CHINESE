package com.example.app_learn_chinese_2025.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.User;

public class SessionManager {
    private static final String PREF_NAME = "UngDungHocTiengTrungSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "ID";
    private static final String KEY_USERNAME = "TenDangNhap";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_FULL_NAME = "HoTen";
    private static final String KEY_PHONE = "SoDienThoai";
    private static final String KEY_ROLE = "VaiTro";
    private static final String KEY_HSK_LEVEL = "TrinhDoHSK";
    private static final String KEY_AVATAR = "HinhDaiDien";
    private static final String KEY_STATUS = "TrangThai";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(User user, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getID()); // Đổi sang putLong
        editor.putString(KEY_USERNAME, user.getTenDangNhap());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getHoTen());
        editor.putString(KEY_PHONE, user.getSoDienThoai());
        editor.putInt(KEY_ROLE, user.getVaiTro());
        editor.putInt(KEY_HSK_LEVEL, user.getTrinhDoHSK());
        editor.putString(KEY_AVATAR, user.getHinhDaiDien());
        editor.putBoolean(KEY_STATUS, user.getTrangThai()); // Đổi sang putBoolean
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getUserDetails() {
        if (!isLoggedIn()) return null;
        try {
            User user = new User();
            user.setID(prefs.getLong(KEY_USER_ID, -1));
            user.setTenDangNhap(prefs.getString(KEY_USERNAME, ""));
            user.setEmail(prefs.getString(KEY_EMAIL, ""));
            user.setHoTen(prefs.getString(KEY_FULL_NAME, ""));
            user.setSoDienThoai(prefs.getString(KEY_PHONE, ""));
            user.setVaiTro(prefs.getInt(KEY_ROLE, -1));
            user.setTrinhDoHSK(prefs.getInt(KEY_HSK_LEVEL, 0));
            user.setHinhDaiDien(prefs.getString(KEY_AVATAR, ""));
            user.setTrangThai(prefs.getBoolean(KEY_STATUS, false));
            Log.d("SessionManager", "User details: HoTen=" + user.getHoTen() + ", Role=" + user.getVaiTro());
            return user;
        } catch (Exception e) {
            Log.e("SessionManager", "Error getting user details: " + e.getMessage(), e);
            return null;
        }
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public int getUserRole() {
        return prefs.getInt(KEY_ROLE, -1);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}