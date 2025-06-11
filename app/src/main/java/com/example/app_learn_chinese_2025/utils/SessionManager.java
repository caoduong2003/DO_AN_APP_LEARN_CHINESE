package com.example.app_learn_chinese_2025.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    // Token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }
    
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }
    
    // User ID
    public void saveUserId(Long userId) {
        editor.putLong(KEY_USER_ID, userId);
        editor.commit();
    }
    
    public Long getUserId() {
        return pref.getLong(KEY_USER_ID, -1);
    }
    
    // Username
    public void saveUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }
    
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }
    
    // Email
    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }
    
    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }
    
    // Role
    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }
    
    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }
    
    // Clear session
    public void clearSession() {
        editor.clear();
        editor.commit();
    }
    
    // Check if user is logged in
    public boolean isLoggedIn() {
        return getToken() != null;
    }
} 