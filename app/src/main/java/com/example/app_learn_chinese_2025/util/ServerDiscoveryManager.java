// 1. ServerDiscoveryManager.java
package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 🚀 Tự động phát hiện và quản lý server URL
 * Hỗ trợ cả máy thật và máy ảo
 */
public class ServerDiscoveryManager {
    private static final String TAG = "SERVER_DISCOVERY";
    private static final String PREF_SERVER_URL = "discovered_server_url";
    private static final String PREF_LAST_DISCOVERY_TIME = "last_discovery_time";
    private static final long DISCOVERY_CACHE_TIME = 30 * 60 * 1000; // 30 phút

    private static ServerDiscoveryManager instance;
    private Context context;
    private SharedPreferences prefs;
    private String currentServerUrl;

    // Danh sách các IP có thể có của server
    private static final String[] POTENTIAL_SERVERS = {
            "10.0.2.2",        // Android Emulator
            "localhost",       // Local test
            "127.0.0.1",       // Local loopback
            "192.168.1.%d",    // Common home network
            "192.168.0.%d",    // Another common network
            "192.168.50.%d",   // Your current network
            "10.0.0.%d",       // Some corporate networks
            "172.16.%d.%d"     // Private network range
    };

    private static final int[] COMMON_PORTS = {8080, 8081, 8082, 9090, 3000};

    public interface ServerDiscoveryCallback {
        void onServerFound(String baseUrl);
        void onServerNotFound(String error);
        void onDiscoveryProgress(String message);
    }

    private ServerDiscoveryManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("server_discovery", Context.MODE_PRIVATE);
        this.currentServerUrl = prefs.getString(PREF_SERVER_URL, null);
    }

    public static synchronized ServerDiscoveryManager getInstance(Context context) {
        if (instance == null) {
            instance = new ServerDiscoveryManager(context);
        }
        return instance;
    }

    /**
     * Lấy server URL hiện tại
     */
    public String getCurrentServerUrl() {
        return currentServerUrl;
    }

    /**
     * Phát hiện server tự động
     */
    public void discoverServer(ServerDiscoveryCallback callback) {
        // Kiểm tra cache trước
        long lastDiscovery = prefs.getLong(PREF_LAST_DISCOVERY_TIME, 0);
        if (currentServerUrl != null &&
                System.currentTimeMillis() - lastDiscovery < DISCOVERY_CACHE_TIME) {
            Log.d(TAG, "Using cached server URL: " + currentServerUrl);
            callback.onServerFound(currentServerUrl);
            return;
        }

        Log.d(TAG, "Starting server discovery...");
        callback.onDiscoveryProgress("Đang tìm kiếm server...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();

        // Test các server có thể có
        List<String> candidateUrls = generateCandidateUrls();

        for (String url : candidateUrls) {
            futures.add(executor.submit(() -> testServerUrl(url)));
        }

        // Chờ kết quả
        new Thread(() -> {
            try {
                String foundUrl = null;
                for (Future<String> future : futures) {
                    try {
                        String result = future.get(2, TimeUnit.SECONDS);
                        if (result != null) {
                            foundUrl = result;
                            break;
                        }
                    } catch (Exception e) {
                        // Ignore individual failures
                    }
                }

                executor.shutdown();

                if (foundUrl != null) {
                    currentServerUrl = foundUrl;
                    prefs.edit()
                            .putString(PREF_SERVER_URL, foundUrl)
                            .putLong(PREF_LAST_DISCOVERY_TIME, System.currentTimeMillis())
                            .apply();

                    Log.d(TAG, "✅ Server found: " + foundUrl);
                    callback.onServerFound(foundUrl);
                } else {
                    Log.e(TAG, "❌ No server found");
                    callback.onServerNotFound("Không tìm thấy server nào");
                }

            } catch (Exception e) {
                Log.e(TAG, "Discovery failed: " + e.getMessage());
                callback.onServerNotFound("Lỗi tìm kiếm server: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Tạo danh sách URL ứng viên
     */
    private List<String> generateCandidateUrls() {
        List<String> urls = new ArrayList<>();

        // Thêm IP hiện tại từ WiFi
        String wifiIp = getWifiIpAddress();
        if (wifiIp != null) {
            String subnet = wifiIp.substring(0, wifiIp.lastIndexOf('.'));
            for (int i = 1; i < 255; i++) {
                for (int port : COMMON_PORTS) {
                    urls.add("http://" + subnet + "." + i + ":" + port + "/");
                }
            }
        }

        // Thêm các server thường gặp
        for (String serverPattern : POTENTIAL_SERVERS) {
            for (int port : COMMON_PORTS) {
                if (serverPattern.contains("%d")) {
                    if (serverPattern.contains("172.16.%d.%d")) {
                        for (int i = 0; i < 32; i++) {
                            for (int j = 1; j < 255; j++) {
                                urls.add("http://" + String.format(serverPattern, i, j) + ":" + port + "/");
                            }
                        }
                    } else {
                        for (int i = 1; i < 255; i++) {
                            urls.add("http://" + String.format(serverPattern, i) + ":" + port + "/");
                        }
                    }
                } else {
                    urls.add("http://" + serverPattern + ":" + port + "/");
                }
            }
        }

        // Ưu tiên một số URL thường gặp
        urls.add(0, "http://10.0.2.2:8080/");          // Android Emulator
        urls.add(1, "http://192.168.50.70:8080/");     // Your current IP
        urls.add(2, "http://localhost:8080/");          // Local

        return urls;
    }

    /**
     * Test một URL cụ thể
     */
    private String testServerUrl(String baseUrl) {
        try {
            URL url = new URL(baseUrl + "api/test/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Log.d(TAG, "✅ Server responded: " + baseUrl);
                return baseUrl;
            } else {
                Log.v(TAG, "❌ Server failed: " + baseUrl + " (HTTP " + responseCode + ")");
            }
        } catch (Exception e) {
            Log.v(TAG, "❌ Server unreachable: " + baseUrl + " (" + e.getMessage() + ")");
        }
        return null;
    }

    /**
     * Lấy IP address từ WiFi
     */
    private String getWifiIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                return String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get WiFi IP: " + e.getMessage());
        }
        return null;
    }

    /**
     * Force refresh server discovery
     */
    public void forceDiscovery(ServerDiscoveryCallback callback) {
        prefs.edit().remove(PREF_LAST_DISCOVERY_TIME).apply();
        currentServerUrl = null;
        discoverServer(callback);
    }

    /**
     * Manually set server URL
     */
    public void setServerUrl(String url, boolean verify) {
        if (verify) {
            new Thread(() -> {
                String validUrl = testServerUrl(url);
                if (validUrl != null) {
                    currentServerUrl = validUrl;
                    prefs.edit()
                            .putString(PREF_SERVER_URL, validUrl)
                            .putLong(PREF_LAST_DISCOVERY_TIME, System.currentTimeMillis())
                            .apply();
                    Log.d(TAG, "✅ Manual server URL set and verified: " + validUrl);
                } else {
                    Log.e(TAG, "❌ Manual server URL verification failed: " + url);
                }
            }).start();
        } else {
            currentServerUrl = url;
            prefs.edit()
                    .putString(PREF_SERVER_URL, url)
                    .putLong(PREF_LAST_DISCOVERY_TIME, System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Manual server URL set (no verification): " + url);
        }
    }
}