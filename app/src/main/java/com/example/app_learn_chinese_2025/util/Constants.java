package com.example.app_learn_chinese_2025.util;

public class Constants {
    // URL cơ sở của API
    public static final String BASE_URL = "http://1.54.173.124:8080/"; // Thay thế bằng URL thực tế của bạn
    
    // Các đường dẫn API
    public static final String API_BAI_GIANG = "api/baigiang/";
    public static final String API_LOAI_BAI_GIANG = "api/loaibaigiang/";
    public static final String API_CAP_DO_HSK = "api/capdohsk/";
    public static final String API_CHU_DE = "api/chude/";
    
    // Các key cho SharedPreferences
    public static final String PREF_NAME = "AppPrefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ROLE = "role";

    // Media URLs
    public static final String VIDEO_BASE_URL = BASE_URL + "api/media/video/";
    public static final String IMAGE_BASE_URL = BASE_URL + "api/media/image/";

    // Các hằng số khác
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_STUDENT = 2;
}