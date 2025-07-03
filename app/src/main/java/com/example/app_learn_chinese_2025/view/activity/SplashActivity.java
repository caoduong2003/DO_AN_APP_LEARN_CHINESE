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

public class SplashActivity extends AppCompatActivity {
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "🚀 SplashActivity started");

        // 🎯 KHỞI TẠO SMART CONSTANTS NGAY ĐẦU TIÊN
        Constants.initialize(this);
        Log.d(TAG, "📡 Smart Constants initialized - auto-detection started");


        authController = new AuthController(this);

        // Delay 2 seconds to show splash screen
        new Handler().postDelayed(this::checkLoginStatus, 2000);
    }

    private void checkLoginStatus() {
        if (authController.isLoggedIn()) {
            // Redirect to appropriate activity based on user role
            redirectBasedOnRole(authController.getUserRole());
        } else {
            // Navigate to login screen
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void redirectBasedOnRole(int role) {
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
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
}