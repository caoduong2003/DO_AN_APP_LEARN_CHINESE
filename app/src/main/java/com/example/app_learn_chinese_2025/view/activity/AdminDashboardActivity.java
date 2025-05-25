package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnLogout;
    private CardView cardManageUsers, cardManageTeachers, cardManageLessons, cardStatistics;

    private SessionManager sessionManager;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupListeners();
        loadUserInfo();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardManageTeachers = findViewById(R.id.cardManageTeachers);
        cardManageLessons = findViewById(R.id.cardManageLessons);
        cardStatistics = findViewById(R.id.cardStatistics);

        sessionManager = new SessionManager(this);
        authController = new AuthController(this);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());

        cardManageUsers.setOnClickListener(v -> {
            // Navigate to user management (Students)
            Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        cardManageTeachers.setOnClickListener(v -> {
            // Navigate to teacher management
            Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        cardManageLessons.setOnClickListener(v -> {
            // Navigate to lesson management
            Toast.makeText(this, "Chức năng quản lý bài giảng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        cardStatistics.setOnClickListener(v -> {
            // Navigate to statistics
            Toast.makeText(this, "Chức năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            tvWelcome.setText("Xin chào " + user.getHoTen() + " (Quản trị viên)");
        }
    }

    private void logout() {
        authController.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}