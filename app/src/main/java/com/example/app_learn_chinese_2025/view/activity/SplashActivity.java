package com.example.app_learn_chinese_2025.view.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private AuthController authController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "🚀 SplashActivity started");

        // 🎯 KHỞI TẠO SMART CONSTANTS NGAY ĐẦU TIÊN
        Constants.initialize(this);
        Log.d(TAG, "📡 Smart Constants initialized - auto-detection started");

        authController = new AuthController(this);
        sessionManager = new SessionManager(this);

        // Delay 2 seconds to show splash screen
        new Handler().postDelayed(this::checkLoginStatus, 2000);
    }

    private void checkLoginStatus() {
        Log.d(TAG, "🔍 Checking login status");

        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "✅ User is logged in");
            // Redirect to appropriate activity based on user role
            redirectBasedOnRole(sessionManager.getUserRole());
        } else if (sessionManager.isGuestMode()) {
            Log.d(TAG, "👤 User is in guest mode");
            // Navigate to guest dashboard
            startActivity(new Intent(SplashActivity.this, GuestDashboardActivity.class));
            finish();
        } else {
            Log.d(TAG, "❌ No active session - showing welcome screen");
            // Navigate to guest welcome screen
            startActivity(new Intent(SplashActivity.this, GuestWelcomeActivity.class));
            finish();
        }
    }

    private void redirectBasedOnRole(int role) {
        Log.d(TAG, "🎯 Redirecting based on role: " + role);
        Intent intent;

        switch (role) {
            case Constants.ROLE_ADMIN:
                intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                break;
            case Constants.ROLE_TEACHER:
                intent = new Intent(SplashActivity.this, TeacherDashboardActivity.class);
                break;
            case Constants.ROLE_STUDENT:
                intent = new Intent(SplashActivity.this, StudentDashboardActivity.class);
                break;
            default:
                Log.w(TAG, "❌ Unknown role: " + role + " - redirecting to welcome");
                intent = new Intent(SplashActivity.this, GuestWelcomeActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
}