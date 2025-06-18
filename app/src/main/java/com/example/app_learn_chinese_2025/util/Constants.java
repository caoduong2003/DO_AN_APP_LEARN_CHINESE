package com.example.app_learn_chinese_2025.util;

public class Constants {
    // URL cơ sở của API - QUAN TRỌNG: phải kết thúc bằng dấu /
    public static final String BASE_URL = "http://1.53.72.37:8080/";

    // Các đường dẫn API
    public static final String API_BAI_GIANG = "api/baigiang/";
    public static final String API_LOAI_BAI_GIANG = "api/loaibaigiang/";
    public static final String API_CAP_DO_HSK = "api/capdohsk/";
    public static final String API_CHU_DE = "api/chude/";

    // Media URLs - SỬA LẠI
    public static final String VIDEO_BASE_URL = BASE_URL + "uploads/videos/";
    public static final String IMAGE_BASE_URL = BASE_URL + "uploads/images/";
    public static final String AUDIO_BASE_URL = BASE_URL + "uploads/audio/";

    // Media URLs - SỬA LẠI THEO CÁCH SERVER SERVE FILE
    public static final String VIDEO_BASE = "";

    // Các key cho SharedPreferences
    public static final String PREF_NAME = "AppPrefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ROLE = "role";

    // Các hằng số khác
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_STUDENT = 2;
    // THÊM METHOD HELPER ĐỂ XỬ LÝ URL
    public static String getFullVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ
        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
            return videoUrl;
        }

        // Nếu bắt đầu bằng /uploads/
        if (videoUrl.startsWith("/uploads/")) {
            return BASE_URL + videoUrl.substring(1); // Bỏ dấu / đầu
        }

        // Nếu bắt đầu bằng uploads/
        if (videoUrl.startsWith("uploads/")) {
            return BASE_URL + videoUrl;
        }

        // Nếu chỉ là tên file
        return VIDEO_BASE_URL + videoUrl;
    }

    public static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/uploads/")) {
            return BASE_URL + imageUrl.substring(1);
        }

        if (imageUrl.startsWith("uploads/")) {
            return BASE_URL + imageUrl;
        }

        return IMAGE_BASE_URL + imageUrl;
    }
}