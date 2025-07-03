package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🚀 Smart Constants - Tự động phát hiện server URL
 * Không cần can thiệp thủ công, hoàn toàn tự động!
 */
public class Constants {
    private static final String TAG = "SMART_CONSTANTS";

    // 🎯 Fallback URLs cho các trường hợp khác nhau
    private static final String EMULATOR_URL = "http://10.0.2.2:8080/";
    private static final String DEFAULT_REAL_DEVICE_URL = "http://192.168.10.69:8080/";
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

    /**
     * 🎯 MAIN METHOD - Lấy BASE_URL (tự động phát hiện)
     * Đây là method chính mà tất cả code khác sẽ gọi
     */
    public static String getBaseUrl() {
        // Nếu đã detect thành công, trả về ngay
        if (detectedServerUrl != null) {
            return detectedServerUrl;
        }

        // Nếu đang trong quá trình detect, trả về fallback
        if (isDetecting) {
            return getFallbackUrl();
        }

        // Bắt đầu auto-detect (async)
        startAutoDetection();

        // Trả về fallback trong lúc chờ
        return getFallbackUrl();
    }

    /**
     * 🔄 Khởi tạo context (gọi từ Application class)
     */
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        Log.d(TAG, "🚀 Smart Constants initialized");

        // Bắt đầu detect ngay
        startAutoDetection();
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
        if (appContext == null) return null;

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
        if (!isEmulator()) {
            return DEFAULT_REAL_DEVICE_URL;
        }
        return EMULATOR_URL;
    }

    /**
     * 🎯 Build image URL
     */
    public static String getCorrectImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        String fileName = extractFileName(imageUrl);
        if (!fileName.endsWith(IMAGE_EXTENSION)) {
            fileName += IMAGE_EXTENSION;
        }

        return getBaseUrl() + API_VIEW_IMAGE + fileName;
    }

    /**
     * 🎯 Build video URL
     */
    public static String getCorrectVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
            return videoUrl;
        }

        String fileName = extractFileName(videoUrl);
        if (!fileName.endsWith(VIDEO_EXTENSION)) {
            fileName += VIDEO_EXTENSION;
        }

        return getBaseUrl() + API_STREAM_VIDEO + fileName;
    }

    /**
     * 🔧 Extract filename từ path
     */
    private static String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        if (filePath.startsWith("/uploads/videos/")) {
            filePath = filePath.substring("/uploads/videos/".length());
        } else if (filePath.startsWith("/uploads/images/")) {
            filePath = filePath.substring("/uploads/images/".length());
        } else if (filePath.startsWith("uploads/videos/")) {
            filePath = filePath.substring("uploads/videos/".length());
        } else if (filePath.startsWith("uploads/images/")) {
            filePath = filePath.substring("uploads/images/".length());
        } else if (filePath.contains("/")) {
            filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
        }

        return filePath;
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
                ", WiFi IP: " + getDeviceWifiIp();
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

    // Compatibility với code cũ
    public static final String BASE_URL = getBaseUrl(); // Deprecated, dùng getBaseUrl() thay thế
}