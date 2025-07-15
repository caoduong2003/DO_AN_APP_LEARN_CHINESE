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

    // 🚀 NEW: Guest mode keys
    private static final String KEY_IS_GUEST = "isGuest";
    private static final String KEY_GUEST_DEVICE_ID = "guestDeviceId";
    private static final String KEY_GUEST_FIRST_ACCESS = "guestFirstAccess";

    private static final String TAG = "SessionManager";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Tạo session cho user đăng nhập
     */
    public void createSession(User user, String token) {
        Log.d(TAG, "Creating user session for: " + user.getTenDangNhap());

        // Clear guest session if exists
        clearGuestSession();

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_GUEST, false);
        editor.putLong(KEY_USER_ID, user.getID());
        editor.putString(KEY_USERNAME, user.getTenDangNhap());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getHoTen());
        editor.putString(KEY_PHONE, user.getSoDienThoai());
        editor.putInt(KEY_ROLE, user.getVaiTro());
        editor.putInt(KEY_HSK_LEVEL, user.getTrinhDoHSK());
        editor.putString(KEY_AVATAR, user.getHinhDaiDien());
        editor.putBoolean(KEY_STATUS, user.getTrangThai());
        editor.putString(KEY_TOKEN, token);
        editor.apply();

        Log.d(TAG, "✅ User session created successfully");
    }

    /**
     * 🚀 NEW: Tạo session cho guest
     */
    public void createGuestSession() {
        Log.d(TAG, "Creating guest session");

        // Clear any existing session
        clearAllSessions();

        String deviceId = generateGuestDeviceId();
        long currentTime = System.currentTimeMillis();

        editor.putBoolean(KEY_IS_GUEST, true);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_GUEST_DEVICE_ID, deviceId);
        editor.putLong(KEY_GUEST_FIRST_ACCESS, currentTime);
        editor.apply();

        Log.d(TAG, "✅ Guest session created with device ID: " + deviceId);
    }

    /**
     * 🚀 NEW: Kiểm tra có phải guest mode không
     */
    public boolean isGuestMode() {
        boolean isGuest = prefs.getBoolean(KEY_IS_GUEST, false);
        Log.d(TAG, "Is guest mode: " + isGuest);
        return isGuest;
    }

    /**
     * Kiểm tra đã đăng nhập chưa (chỉ user thật, không tính guest)
     */
    public boolean isLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !isGuestMode();
        Log.d(TAG, "Is logged in: " + loggedIn);
        return loggedIn;
    }

    /**
     * 🚀 NEW: Kiểm tra có session nào không (guest hoặc user)
     */
    public boolean hasAnySession() {
        return isLoggedIn() || isGuestMode();
    }

    /**
     * Lấy thông tin user (chỉ cho logged in user)
     */
    public User getUserDetails() {
        if (!isLoggedIn()) {
            Log.d(TAG, "No logged in user - returning null");
            return null;
        }

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
            Log.d(TAG, "User details: HoTen=" + user.getHoTen() + ", Role=" + user.getVaiTro());
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user details: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🚀 NEW: Lấy Guest Device ID
     */
    public String getGuestDeviceId() {
        return prefs.getString(KEY_GUEST_DEVICE_ID, "");
    }

    /**
     * 🚀 NEW: Lấy thời gian first access của guest
     */
    public long getGuestFirstAccess() {
        return prefs.getLong(KEY_GUEST_FIRST_ACCESS, 0);
    }

    /**
     * Lấy token (chỉ cho logged in user)
     */
    public String getToken() {
        if (!isLoggedIn()) {
            return "";
        }
        return prefs.getString(KEY_TOKEN, "");
    }

    /**
     * Lấy user role
     */
    public int getUserRole() {
        return prefs.getInt(KEY_ROLE, -1);
    }

    /**
     * 🚀 NEW: Chuyển từ guest sang user
     */
    public void upgradeFromGuestToUser(User user, String token) {
        Log.d(TAG, "Upgrading from guest to user: " + user.getTenDangNhap());

        // Lưu guest data trước khi clear (nếu cần)
        String guestDeviceId = getGuestDeviceId();
        long guestFirstAccess = getGuestFirstAccess();

        // Tạo user session
        createSession(user, token);

        // Log thông tin upgrade
        Log.d(TAG, "✅ Successfully upgraded from guest (device: " + guestDeviceId +
                ") to user: " + user.getTenDangNhap());
    }

    /**
     * 🚀 NEW: Clear guest session
     */
    public void clearGuestSession() {
        Log.d(TAG, "Clearing guest session");
        editor.remove(KEY_IS_GUEST);
        editor.remove(KEY_GUEST_DEVICE_ID);
        editor.remove(KEY_GUEST_FIRST_ACCESS);
        editor.apply();
    }

    /**
     * 🚀 NEW: Clear all sessions
     */
    public void clearAllSessions() {
        Log.d(TAG, "Clearing all sessions");
        editor.clear();
        editor.apply();
    }

    /**
     * Đăng xuất (clear user session, có thể giữ guest option)
     */
    public void logout() {
        Log.d(TAG, "Logging out user");
        clearAllSessions();
    }

    /**
     * 🚀 NEW: Đăng xuất và chuyển về guest mode
     */
    public void logoutToGuest() {
        Log.d(TAG, "Logging out to guest mode");

        // Clear user session
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_FULL_NAME);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_ROLE);
        editor.remove(KEY_HSK_LEVEL);
        editor.remove(KEY_AVATAR);
        editor.remove(KEY_STATUS);
        editor.remove(KEY_TOKEN);
        editor.apply();

        // Create new guest session
        createGuestSession();

        Log.d(TAG, "✅ Logged out to guest mode");
    }

    /**
     * 🚀 NEW: Tạo unique device ID cho guest
     */
    private String generateGuestDeviceId() {
        String deviceId = "guest_" + System.currentTimeMillis() + "_" +
                (int)(Math.random() * 10000);
        Log.d(TAG, "Generated guest device ID: " + deviceId);
        return deviceId;
    }

    /**
     * 🚀 NEW: Lấy display name cho current session
     */
    public String getDisplayName() {
        if (isLoggedIn()) {
            User user = getUserDetails();
            return user != null ? user.getHoTen() : "User";
        } else if (isGuestMode()) {
            return "Khách";
        } else {
            return "Unknown";
        }
    }

    /**
     * 🚀 NEW: Lấy session type
     */
    public String getSessionType() {
        if (isLoggedIn()) {
            return "USER";
        } else if (isGuestMode()) {
            return "GUEST";
        } else {
            return "NONE";
        }
    }

    public void clearSession() {
        String sessionType = getSessionType();
        Log.d(TAG, "Clearing " + sessionType + " session");

        // Clear tất cả dữ liệu session
        editor.clear();
        editor.apply();

        Log.d(TAG, "✅ Session cleared successfully");
    }
}