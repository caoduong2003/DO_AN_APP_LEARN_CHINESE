package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class Constants {
    private static final String TAG = "SMART_CONSTANTS";

    // 🎯 Fallback URLs cho các trường hợp khác nhau
    private static final String EMULATOR_URL = "http://10.0.2.2:8080/";
    private static final String DEFAULT_REAL_DEVICE_URL = "http://192.168.10.115:8080/";
    private static final String LOCALHOST_URL = "http://localhost:8080/";

    // 🔄 Dynamic server URL - sẽ được tự động detect
    private static volatile String detectedServerUrl = null;
    private static volatile boolean isDetecting = false;
    private static final AtomicBoolean hasDetected = new AtomicBoolean(false);
    private static Context appContext = null;

    // 📱 Danh sách các IP patterns để test
    private static final String[] IP_PATTERNS = {
            "10.0.2.2",        // Android Emulator
            "127.0.0.1",       // Localhost
            "192.168.1.%d",    // Common home network
            "192.168.0.%d",    // Another common network
            "192.168.50.%d",   // Your current network
            "192.168.10.%d",   // Your current network
            "10.0.0.%d",       // Corporate network
            "172.16.0.%d"      // Private network
    };

    private static final int[] PORTS = {8080, 8081, 9090, 3000};

    // API paths (không thay đổi)
    public static final String API_BAI_GIANG = "api/baigiang/";
    public static final String API_LOAI_BAI_GIANG = "api/loaibaigiang/";
    public static final String API_CAP_DO_HSK = "api/capdohsk/";
    public static final String API_CHU_DE = "api/chude/";
    public static final String API_UPLOAD_IMAGE = "api/media/upload/image";
    public static final String API_UPLOAD_VIDEO = "api/media/upload/video";
    public static final String API_VIEW_IMAGE = "api/media/image/";
    public static final String API_STREAM_VIDEO = "api/media/video/";
    public static final String API_DEBUG_TEST = "api/files/debug/test";
    public static final String API_DEBUG_LIST = "api/files/debug/list";
    public static final String API_TEST_PING = "api/test/ping";

    // SharedPreferences keys
    public static final String PREF_NAME = "AppPrefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ROLE = "role";

    // Role constants
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_STUDENT = 2;

    // File extensions
    public static final String IMAGE_EXTENSION = ".png";
    public static final String VIDEO_EXTENSION = ".mp4";

    // ✅ FIXED: Remove static field that causes circular dependency
    // public static final String BASE_URL = getBaseUrl(); // ❌ REMOVED

    /**
     * 🚀 Enhanced initialize method với AutoIPManager
     */
    public static void initialize(Context context) {
        Log.d(TAG, "🚀 Smart Constants initialize started");

        // ✅ CRITICAL: Set context FIRST
        appContext = context.getApplicationContext();
        Log.d(TAG, "📱 Context initialized");

        // ✅ Start auto detection
        startAutoDetection();


        Log.d(TAG, "✅ Smart Constants initialized successfully");
    }

    /**
     * 🔄 Get server URL với auto fallback
     */
    public static String getBaseUrl() {
        // ✅ SAFETY: Check if initialized
        if (appContext == null) {
            Log.w(TAG, "⚠️ Constants not initialized, using default");
            return DEFAULT_REAL_DEVICE_URL;
        }

        // 1. Try detected URL first
        if (detectedServerUrl != null && !detectedServerUrl.isEmpty()) {
            return detectedServerUrl;
        }


        // 3. Fallback logic
        if (isEmulator()) {
            return EMULATOR_URL;
        }

        return DEFAULT_REAL_DEVICE_URL;
    }

    /**
     * 🔍 Bắt đầu quá trình auto-detection
     */
    private static void startAutoDetection() {
        if (isDetecting || hasDetected.get()) {
            return;
        }

        isDetecting = true;
        Log.d(TAG, "🔍 Starting server auto-detection...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String foundUrl = detectServerUrl();
                if (foundUrl != null) {
                    detectedServerUrl = foundUrl;
                    hasDetected.set(true);
                    Log.d(TAG, "✅ Server auto-detected: " + foundUrl);
                } else {
                    Log.w(TAG, "❌ No server found, using fallback");
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Detection failed: " + e.getMessage());
            } finally {
                isDetecting = false;
                executor.shutdown();
            }
        });
    }

    /**
     * 🎯 Logic chính để detect server
     */
    private static String detectServerUrl() {
        Log.d(TAG, "🔍 Scanning for servers...");

        // 1. Test emulator IP trước (nếu là emulator)
        if (isEmulator()) {
            Log.d(TAG, "📱 Detected emulator, testing emulator IP...");
            if (testUrl(EMULATOR_URL)) {
                return EMULATOR_URL;
            }
        }

        // 2. Test localhost
        if (testUrl(LOCALHOST_URL)) {
            return LOCALHOST_URL;
        }

        // 3. Test IP hiện tại của device (từ WiFi)
        String deviceIp = getDeviceWifiIp();
        if (deviceIp != null) {
            String subnet = deviceIp.substring(0, deviceIp.lastIndexOf('.'));
            Log.d(TAG, "📡 Device subnet: " + subnet + ".x");

            // Test một số IP trong subnet
            for (int i = 1; i <= 254; i++) {
                for (int port : PORTS) {
                    String testUrl = "http://" + subnet + "." + i + ":" + port + "/";
                    if (testUrl(testUrl)) {
                        return testUrl;
                    }
                }
            }
        }

        // 4. Test các pattern IP thường gặp
        for (String pattern : IP_PATTERNS) {
            if (pattern.contains("%d")) {
                for (int i = 1; i <= 254; i++) {
                    for (int port : PORTS) {
                        String ip = String.format(pattern, i);
                        String testUrl = "http://" + ip + ":" + port + "/";
                        if (testUrl(testUrl)) {
                            return testUrl;
                        }
                    }
                }
            } else {
                for (int port : PORTS) {
                    String testUrl = "http://" + pattern + ":" + port + "/";
                    if (testUrl(testUrl)) {
                        return testUrl;
                    }
                }
            }
        }

        Log.w(TAG, "❌ No server found in scan");
        return null;
    }

    /**
     * 🧪 Test một URL cụ thể
     */
    private static boolean testUrl(String baseUrl) {
        try {
            // Test ping endpoint
            URL url = new URL(baseUrl + API_TEST_PING);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(800);  // Timeout ngắn để scan nhanh
            connection.setReadTimeout(800);

            int responseCode = connection.getResponseCode();
            boolean isValid = (responseCode == 200);

            if (isValid) {
                Log.d(TAG, "✅ Found server at: " + baseUrl);
            }

            connection.disconnect();
            return isValid;

        } catch (Exception e) {
            // Không log error để tránh spam (vì sẽ test rất nhiều URL)
            return false;
        }
    }

    /**
     * 🤖 Kiểm tra có phải emulator không
     */
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.startsWith("generic") ||
                android.os.Build.FINGERPRINT.startsWith("unknown") ||
                android.os.Build.MODEL.contains("google_sdk") ||
                android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MODEL.contains("Android SDK built for x86") ||
                android.os.Build.MANUFACTURER.contains("Genymotion") ||
                (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic")) ||
                "google_sdk".equals(android.os.Build.PRODUCT);
    }

    /**
     * 📡 Lấy IP của device từ WiFi
     */
    private static String getDeviceWifiIp() {
        if (appContext == null) {
            Log.w(TAG, "⚠️ Cannot get WiFi IP - context not initialized");
            return null;
        }

        try {
            WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                if (ipAddress != 0) {
                    return String.format("%d.%d.%d.%d",
                            (ipAddress & 0xff),
                            (ipAddress >> 8 & 0xff),
                            (ipAddress >> 16 & 0xff),
                            (ipAddress >> 24 & 0xff));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get WiFi IP: " + e.getMessage());
        }
        return null;
    }

    /**
     * 🔄 Fallback URL logic
     */
    private static String getFallbackUrl() {
        if (isEmulator()) {
            return EMULATOR_URL;
        }
        return DEFAULT_REAL_DEVICE_URL;
    }

    /**
     * 🔄 Update discovered server từ AutoIPManager
     */
    public static void updateDiscoveredServer(String serverURL) {
        detectedServerUrl = serverURL;
        hasDetected.set(true);
        Log.d(TAG, "📡 Server updated by AutoIPManager: " + serverURL);

        // 🔄 Force recreate retrofit clients
        try {
            Class<?> retrofitClientClass = Class.forName("com.example.app_learn_chinese_2025.model.remote.RetrofitClient");
            java.lang.reflect.Method forceRecreateMethod = retrofitClientClass.getDeclaredMethod("forceRecreate");
            forceRecreateMethod.invoke(null);
            Log.d(TAG, "🔄 RetrofitClient instances recreated");
        } catch (Exception e) {
            Log.e(TAG, "Failed to recreate RetrofitClient: " + e.getMessage());
        }
    }

    /**
     * 🎯 Build image URL
     */
    public static String getCorrectImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ, return ngay
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // ✅ FIXED: Extract filename đúng cách
        String fileName = extractFileName(imageUrl);

        // ✅ FIXED: Không tự động thêm .png extension
        // Vì file có thể là .jpg, .jpeg, .png, etc.
        // if (!fileName.endsWith(IMAGE_EXTENSION)) {
        //     fileName += IMAGE_EXTENSION;
        // }

        // ✅ CORRECT URL: http://192.168.10.69:8080/api/media/image/book1.png
        return getBaseUrl() + API_VIEW_IMAGE + fileName;
    }

    /**
     * 🎯 Build video URL
     */
//    public static String getCorrectVideoUrl(String videoUrl) {
//        if (videoUrl == null || videoUrl.isEmpty()) {
//            return null;
//        }
//
//        // Nếu đã là URL đầy đủ, return ngay
//        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
//            return videoUrl;
//        }
//
//        // ✅ FIXED: Extract filename đúng cách
//        String fileName = extractFileName(videoUrl);
//
//        // ✅ FIXED: Không tự động thêm .mp4 extension
//        // Vì file có thể là .mp4, .avi, .mkv, etc.
//        // if (!fileName.endsWith(VIDEO_EXTENSION)) {
//        //     fileName += VIDEO_EXTENSION;
//        // }
//
//        // ✅ CORRECT URL: http://192.168.10.69:8080/api/media/video/1.Lesson_1.mp4
//        return getBaseUrl() + API_STREAM_VIDEO + fileName;
//    }

    public static String getCorrectVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return "";
        }

        // If already a full URL, return as is
        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
            return videoUrl;
        }

        // Extract filename from various input formats
        String fileName = extractFileName(videoUrl);

        // ✅ CORRECT URL: http://10.0.2.2:8080/api/media/video/1.Lesson_1.mp4
        return getBaseUrl() + API_STREAM_VIDEO + fileName;
    }

    /**
     * 🔧 Extract filename từ path
     */
    private static String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // ✅ FIXED: Handle các format input khác nhau
        String fileName = filePath;

        // Remove various prefixes
        if (fileName.startsWith("/uploads/videos/")) {
            fileName = fileName.substring("/uploads/videos/".length());
        } else if (fileName.startsWith("/uploads/images/")) {
            fileName = fileName.substring("/uploads/images/".length());
        } else if (fileName.startsWith("uploads/videos/")) {
            fileName = fileName.substring("uploads/videos/".length());
        } else if (fileName.startsWith("uploads/images/")) {
            fileName = fileName.substring("uploads/images/".length());
        } else if (fileName.startsWith("/api/media/video/")) {
            fileName = fileName.substring("/api/media/video/".length());
        } else if (fileName.startsWith("/api/media/image/")) {
            fileName = fileName.substring("/api/media/image/".length());
        } else if (fileName.startsWith("api/media/video/")) {
            fileName = fileName.substring("api/media/video/".length());
        } else if (fileName.startsWith("api/media/image/")) {
            fileName = fileName.substring("api/media/image/".length());
        } else if (fileName.contains("/")) {
            // Lấy phần cuối sau dấu / cuối cùng
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        return fileName;
    }

    // 🔧 Helper methods để build URLs
    public static String getImageUploadUrl() {
        return getBaseUrl() + API_UPLOAD_IMAGE;
    }

    public static String getVideoUploadUrl() {
        return getBaseUrl() + API_UPLOAD_VIDEO;
    }

    public static String getTestPingUrl() {
        return getBaseUrl() + API_TEST_PING;
    }

    public static String getDebugTestUrl() {
        return getBaseUrl() + API_DEBUG_TEST;
    }

    public static String getDebugListUrl() {
        return getBaseUrl() + API_DEBUG_LIST;
    }

    /**
     * 🔄 Force re-detection (nếu cần)
     */
    public static void forceReDetection() {
        detectedServerUrl = null;
        hasDetected.set(false);
        isDetecting = false;
        Log.d(TAG, "🔄 Forcing re-detection...");
        startAutoDetection();
    }

    /**
     * 📊 Debug info
     */
    public static String getDebugInfo() {
        return "Detected: " + detectedServerUrl +
                ", Fallback: " + getFallbackUrl() +
                ", Current: " + getBaseUrl() +
                ", Emulator: " + isEmulator() +
                ", WiFi IP: " + getDeviceWifiIp() +
                ", Context: " + (appContext != null ? "OK" : "NULL");
    }

    // Legacy support
    @Deprecated
    public static String getFullVideoUrl(String videoUrl) {
        return getCorrectVideoUrl(videoUrl);
    }

    @Deprecated
    public static String getFullImageUrl(String imageUrl) {
        return getCorrectImageUrl(imageUrl);
    }

    public static void debugMediaURLs() {
        Log.d("MEDIA_DEBUG", "=== MEDIA URL DEBUG ===");

        // Test cases
        String[] testImages = {
                "book1.png",
                "/uploads/images/book1.png",
                "uploads/images/book1.png",
                "api/media/image/book1.png",
                "/api/media/image/book1.png"
        };

        String[] testVideos = {
                "1.Lesson_1.mp4",
                "/uploads/videos/1.Lesson_1.mp4",
                "uploads/videos/1.Lesson_1.mp4",
                "api/media/video/1.Lesson_1.mp4",
                "/api/media/video/1.Lesson_1.mp4"
        };

        Log.d("MEDIA_DEBUG", "Base URL: " + getBaseUrl());

        Log.d("MEDIA_DEBUG", "--- IMAGE URLs ---");
        for (String input : testImages) {
            String result = getCorrectImageUrl(input);
            Log.d("MEDIA_DEBUG", "Input: " + input + " -> " + result);
        }

        Log.d("MEDIA_DEBUG", "--- VIDEO URLs ---");
        for (String input : testVideos) {
            String result = getCorrectVideoUrl(input);
            Log.d("MEDIA_DEBUG", "Input: " + input + " -> " + result);
        }

        Log.d("MEDIA_DEBUG", "==================");
    }

    public static void debugVideoURL(String input) {
        String result = getCorrectVideoUrl(input);
        Log.d("VIDEO_URL_DEBUG", "Input: '" + input + "' -> Output: '" + result + "'");

        // Also log the constants being used
        Log.d("VIDEO_URL_DEBUG", "Base URL: " + getBaseUrl());
        Log.d("VIDEO_URL_DEBUG", "Stream Video Path: " + API_STREAM_VIDEO);
    }

    public static void testVideoUrl(String videoUrl, Context context) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(videoUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();

                // Test with range request (like real video players do)
                connection.setRequestProperty("Range", "bytes=0-1024");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                String contentType = connection.getContentType();
                String contentRange = connection.getHeaderField("Content-Range");
                String acceptRanges = connection.getHeaderField("Accept-Ranges");

                Log.d("VIDEO_TEST", "🎯 === VIDEO URL TEST RESULTS ===");
                Log.d("VIDEO_TEST", "URL: " + videoUrl);
                Log.d("VIDEO_TEST", "Response Code: " + responseCode);
                Log.d("VIDEO_TEST", "Content-Type: " + contentType);
                Log.d("VIDEO_TEST", "Content-Range: " + contentRange);
                Log.d("VIDEO_TEST", "Accept-Ranges: " + acceptRanges);
                Log.d("VIDEO_TEST", "Content-Length: " + connection.getContentLength());

                if (responseCode == 206) {
                    Log.d("VIDEO_TEST", "✅ Server supports range requests (206) - Good for streaming!");
                } else if (responseCode == 200) {
                    Log.d("VIDEO_TEST", "⚠️ Server returns full content (200) - May work but not optimal");
                } else {
                    Log.e("VIDEO_TEST", "❌ Unexpected response code: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e("VIDEO_TEST", "❌ Video URL test failed: " + e.getMessage(), e);
            }
        }).start();
    }
}