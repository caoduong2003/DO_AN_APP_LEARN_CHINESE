package com.example.app_learn_chinese_2025.util;

public class Constants {
    // URL cơ sở của API - QUAN TRỌNG: phải kết thúc bằng dấu /
    public static final String BASE_URL = "http://1.53.72.37:8080/";

    // Các đường dẫn API cũ
    public static final String API_BAI_GIANG = "api/baigiang/";
    public static final String API_LOAI_BAI_GIANG = "api/loaibaigiang/";
    public static final String API_CAP_DO_HSK = "api/capdohsk/";
    public static final String API_CHU_DE = "api/chude/";

    // 🎯 NEW MEDIA API ENDPOINTS
    // Upload endpoints
    public static final String API_UPLOAD_IMAGE = "api/media/upload/image";
    public static final String API_UPLOAD_VIDEO = "api/media/upload/video";

    // View/Stream endpoints
    public static final String API_VIEW_IMAGE = "api/media/image/";
    public static final String API_STREAM_VIDEO = "api/media/video/";

    // Debug endpoints
    public static final String API_DEBUG_TEST = "api/files/debug/test";
    public static final String API_DEBUG_LIST = "api/files/debug/list";

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

    // File extensions
    public static final String IMAGE_EXTENSION = ".png";
    public static final String VIDEO_EXTENSION = ".mp4";

    /**
     * 🎯 FIXED: Build correct image URL using new media API
     * API: GET /api/media/image/{fileName}
     */
    public static String getCorrectImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // Extract filename từ path
        String fileName = extractFileName(imageUrl);

        // Đảm bảo có extension .png
        if (!fileName.endsWith(IMAGE_EXTENSION)) {
            fileName += IMAGE_EXTENSION;
        }

        // Build URL với API mới: /api/media/image/{fileName}
        return BASE_URL + API_VIEW_IMAGE + fileName;
    }

    /**
     * 🎯 FIXED: Build correct video URL using new media API
     * API: GET /api/media/video/{fileName}
     */
    public static String getCorrectVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ
        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
            return videoUrl;
        }

        // Extract filename từ path
        String fileName = extractFileName(videoUrl);

        // Đảm bảo có extension .mp4
        if (!fileName.endsWith(VIDEO_EXTENSION)) {
            fileName += VIDEO_EXTENSION;
        }

        // Build URL với API mới: /api/media/video/{fileName}
        return BASE_URL + API_STREAM_VIDEO + fileName;
    }

    /**
     * Extract file name từ đường dẫn đầy đủ
     */
    private static String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // Loại bỏ các prefix thường gặp
        if (filePath.startsWith("/uploads/videos/")) {
            filePath = filePath.substring("/uploads/videos/".length());
        } else if (filePath.startsWith("/uploads/images/")) {
            filePath = filePath.substring("/uploads/images/".length());
        } else if (filePath.startsWith("uploads/videos/")) {
            filePath = filePath.substring("uploads/videos/".length());
        } else if (filePath.startsWith("uploads/images/")) {
            filePath = filePath.substring("uploads/images/".length());
        } else if (filePath.contains("/")) {
            // Lấy phần cuối sau dấu / cuối cùng
            filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
        }

        return filePath;
    }

    /**
     * 🎯 Build upload URL cho image
     */
    public static String getImageUploadUrl() {
        return BASE_URL + API_UPLOAD_IMAGE;
    }

    /**
     * 🎯 Build upload URL cho video
     */
    public static String getVideoUploadUrl() {
        return BASE_URL + API_UPLOAD_VIDEO;
    }

    /**
     * 🎯 Build debug test URL
     */
    public static String getDebugTestUrl() {
        return BASE_URL + API_DEBUG_TEST;
    }

    /**
     * 🎯 Build debug list URL
     */
    public static String getDebugListUrl() {
        return BASE_URL + API_DEBUG_LIST;
    }

    /**
     * Test connectivity to server
     */
    public static String getConnectivityTestUrl() {
        return getDebugTestUrl();
    }

    // LEGACY SUPPORT - Các method cũ để backward compatibility
    @Deprecated
    public static String getFullVideoUrl(String videoUrl) {
        return getCorrectVideoUrl(videoUrl);
    }

    @Deprecated
    public static String getFullImageUrl(String imageUrl) {
        return getCorrectImageUrl(imageUrl);
    }
}