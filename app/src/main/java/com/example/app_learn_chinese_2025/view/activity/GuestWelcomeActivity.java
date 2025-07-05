package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.util.SessionManager;

/**
 * 🚀 Welcome screen cho Guest Mode
 */
public class GuestWelcomeActivity extends AppCompatActivity {
    private static final String TAG = "GuestWelcomeActivity";

    private TextView tvTitle, tvSubtitle, tvDescription;
    private Button btnStartLearning, btnLogin, btnRegister;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_welcome);

        Log.d(TAG, "🚀 GuestWelcomeActivity created");

        initViews();
        setupListeners();
        setupContent();

        sessionManager = new SessionManager(this);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnStartLearning = findViewById(R.id.btnStartLearning);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupContent() {
        tvTitle.setText("Chào mừng đến với\nỨng dụng Học Tiếng Trung");
        tvSubtitle.setText("Trải nghiệm học tiếng Trung hiệu quả với phương pháp hiện đại");
        tvDescription.setText("Bạn có thể trải nghiệm miễn phí:\n\n" +
                "• 3 bài giảng mỗi ngày\n" +
                "• 5 từ vựng mỗi bài giảng\n" +
                "• 10 lần dịch thuật mỗi ngày\n" +
                "• Truy cập tất cả chủ đề và cấp độ\n\n" +
                "Đăng ký để có trải nghiệm đầy đủ!");

        Log.d(TAG, "✅ Content setup complete");
    }

    private void setupListeners() {
        btnStartLearning.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User chose to start learning as guest");
            startGuestMode();
        });

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User chose to login");
            startLoginActivity();
        });

        btnRegister.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User chose to register");
            startRegisterActivity();
        });

        Log.d(TAG, "✅ Listeners setup complete");
    }

    /**
     * 🎯 Bắt đầu guest mode
     */
    private void startGuestMode() {
        Log.d(TAG, "Creating guest session and starting guest dashboard");

        // Tạo guest session
        sessionManager.createGuestSession();

        // Chuyển đến Guest Dashboard
        Intent intent = new Intent(this, GuestDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 🎯 Chuyển đến màn hình đăng nhập
     */
    private void startLoginActivity() {
        Log.d(TAG, "Starting login activity");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // Không finish() để user có thể back về welcome
    }

    /**
     * 🎯 Chuyển đến màn hình đăng ký
     */
    private void startRegisterActivity() {
        Log.d(TAG, "Starting register activity");

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        // Không finish() để user có thể back về welcome
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GuestWelcomeActivity resumed");

        // Kiểm tra nếu user đã đăng nhập, redirect
        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "User is logged in, redirecting to appropriate dashboard");
            redirectBasedOnRole();
        }
    }

    /**
     * 🎯 Redirect dựa trên role
     */
    private void redirectBasedOnRole() {
        int role = sessionManager.getUserRole();
        Intent intent;

        switch (role) {
            case 0: // Admin
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case 1: // Teacher
                intent = new Intent(this, TeacherDashboardActivity.class);
                break;
            case 2: // Student
                intent = new Intent(this, StudentDashboardActivity.class);
                break;
            default:
                Log.w(TAG, "Unknown role: " + role);
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn thoát ứng dụng?")
                .setPositiveButton("Thoát", (dialog, id) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Hủy", (dialog, id) -> {
                    dialog.dismiss();
                });
        builder.create().show();
    }
}