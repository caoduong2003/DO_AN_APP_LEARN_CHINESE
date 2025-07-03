package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 🚀 AutoIPManager - Tự động quản lý và detect IP server
 * Không cần thay đổi config file thủ công!
 */
public class AutoIPManager {
    private static final String TAG = "AUTO_IP_MANAGER";
    private static final String PREF_NAME = "auto_ip_prefs";
    private static final String KEY_KNOWN_IPS = "known_server_ips";
    private static final String KEY_CURRENT_SERVER = "current_server_ip";

    private static AutoIPManager instance;
    private Context context;
    private SharedPreferences prefs;
    private Set<String> knownServerIPs;
    private String currentServerIP;

    private AutoIPManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadKnownIPs();
    }

    public static synchronized AutoIPManager getInstance(Context context) {
        if (instance == null) {
            instance = new AutoIPManager(context);
        }
        return instance;
    }

    /**
     * 🔍 Tự động detect và register server IP
     */
    public void autoDetectAndRegisterServerIP() {
        Log.d(TAG, "🔍 Starting auto IP detection...");

        new Thread(() -> {
            // 1. Get current WiFi network
            String currentSubnet = getCurrentWiFiSubnet();
            if (currentSubnet != null) {
                Log.d(TAG, "📡 Current subnet: " + currentSubnet);
                scanSubnetForServer(currentSubnet);
            }

            // 2. Test known IPs
            testKnownIPs();

            // 3. Scan common ranges
            scanCommonRanges();

        }).start();
    }

    /**
     * 📡 Lấy subnet hiện tại từ WiFi
     */
    private String getCurrentWiFiSubnet() {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                if (ipAddress != 0) {
                    String deviceIP = String.format("%d.%d.%d.%d",
                            (ipAddress & 0xff),
                            (ipAddress >> 8 & 0xff),
                            (ipAddress >> 16 & 0xff),
                            (ipAddress >> 24 & 0xff));

                    // Extract subnet (first 3 octets)
                    String subnet = deviceIP.substring(0, deviceIP.lastIndexOf('.'));
                    Log.d(TAG, "📱 Device IP: " + deviceIP + ", Subnet: " + subnet);
                    return subnet;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to get WiFi subnet: " + e.getMessage());
        }
        return null;
    }

    /**
     * 🔍 Scan subnet để tìm server
     */
    private void scanSubnetForServer(String subnet) {
        Log.d(TAG, "🔍 Scanning subnet: " + subnet + ".x");

        // Test common server IPs in subnet
        int[] commonLastOctets = {1, 2, 3, 4, 5, 10, 20, 50, 69, 70, 96, 100, 200, 254};
        int[] commonPorts = {8080, 8081, 9090, 3000};

        for (int lastOctet : commonLastOctets) {
            for (int port : commonPorts) {
                String serverIP = subnet + "." + lastOctet;
                String serverURL = "http://" + serverIP + ":" + port + "/";

                if (testServerURL(serverURL)) {
                    registerServerIP(serverIP, port);
                    Log.d(TAG, "✅ Found server in current subnet: " + serverURL);
                    return; // Found one, stop scanning
                }
            }
        }

        Log.d(TAG, "❌ No server found in current subnet: " + subnet);
    }

    /**
     * 🧪 Test known IPs
     */
    private void testKnownIPs() {
        Log.d(TAG, "🧪 Testing known IPs...");

        for (String knownIP : knownServerIPs) {
            String[] parts = knownIP.split(":");
            if (parts.length == 2) {
                String ip = parts[0];
                String port = parts[1];
                String serverURL = "http://" + ip + ":" + port + "/";

                if (testServerURL(serverURL)) {
                    updateCurrentServer(ip, Integer.parseInt(port));
                    Log.d(TAG, "✅ Known server still active: " + serverURL);
                    return;
                }
            }
        }

        Log.d(TAG, "❌ No known IPs are active");
    }

    /**
     * 🔍 Scan common IP ranges
     */
    private void scanCommonRanges() {
        Log.d(TAG, "🔍 Scanning common ranges...");

        String[] commonRanges = {
                "192.168.1",   // Most common home network
                "192.168.0",   // Another common range
                "192.168.10",  // Your current range
                "192.168.50",  // Alternative range
                "10.0.0",      // Corporate
                "172.16.0"     // Private
        };

        int[] testIPs = {1, 2, 69, 70, 96, 100};
        int[] ports = {8080, 8081, 9090};

        for (String range : commonRanges) {
            for (int ip : testIPs) {
                for (int port : ports) {
                    String serverURL = "http://" + range + "." + ip + ":" + port + "/";

                    if (testServerURL(serverURL)) {
                        registerServerIP(range + "." + ip, port);
                        Log.d(TAG, "✅ Found server in common range: " + serverURL);
                        return;
                    }
                }
            }
        }

        Log.d(TAG, "❌ No server found in common ranges");
    }

    /**
     * 🧪 Test server URL
     */
    private boolean testServerURL(String baseURL) {
        try {
            URL url = new URL(baseURL + "api/test/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);

            int responseCode = connection.getResponseCode();
            boolean isValid = (responseCode == 200);

            connection.disconnect();
            return isValid;

        } catch (Exception e) {
            // Silent fail for scanning
            return false;
        }
    }

    /**
     * 📝 Register new server IP
     */
    private void registerServerIP(String ip, int port) {
        String serverEntry = ip + ":" + port;
        knownServerIPs.add(serverEntry);

        updateCurrentServer(ip, port);
        saveKnownIPs();

        Log.d(TAG, "📝 Registered new server: " + serverEntry);

        // Notify other components
        Constants.updateDiscoveredServer("http://" + ip + ":" + port + "/");
    }

    /**
     * 🔄 Update current server
     */
    private void updateCurrentServer(String ip, int port) {
        currentServerIP = ip + ":" + port;
        prefs.edit().putString(KEY_CURRENT_SERVER, currentServerIP).apply();

        Log.d(TAG, "🔄 Updated current server: " + currentServerIP);
    }

    /**
     * 💾 Load known IPs from storage
     */
    private void loadKnownIPs() {
        knownServerIPs = prefs.getStringSet(KEY_KNOWN_IPS, new HashSet<>());
        if (knownServerIPs == null) {
            knownServerIPs = new HashSet<>();
        }

        currentServerIP = prefs.getString(KEY_CURRENT_SERVER, null);

        Log.d(TAG, "💾 Loaded " + knownServerIPs.size() + " known IPs");
        Log.d(TAG, "📡 Current server: " + currentServerIP);
    }

    /**
     * 💾 Save known IPs to storage
     */
    private void saveKnownIPs() {
        prefs.edit().putStringSet(KEY_KNOWN_IPS, knownServerIPs).apply();
        Log.d(TAG, "💾 Saved " + knownServerIPs.size() + " known IPs");
    }

    /**
     * 📊 Get current server info
     */
    public String getCurrentServerURL() {
        if (currentServerIP != null) {
            return "http://" + currentServerIP + "/";
        }
        return null;
    }

    /**
     * 🗑️ Clear all known IPs (for debugging)
     */
    public void clearKnownIPs() {
        knownServerIPs.clear();
        currentServerIP = null;
        prefs.edit().clear().apply();
        Log.d(TAG, "🗑️ Cleared all known IPs");
    }

    /**
     * 📋 Get debug info
     */
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Current Server: ").append(currentServerIP).append("\n");
        info.append("Known IPs (").append(knownServerIPs.size()).append("):\n");
        for (String ip : knownServerIPs) {
            info.append("  - ").append(ip).append("\n");
        }
        info.append("WiFi Subnet: ").append(getCurrentWiFiSubnet());
        return info.toString();
    }
}