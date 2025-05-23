package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.ViewPagerAdapter;
import com.example.app_learn_chinese_2025.view.fragment.BaiGiangListFragment;
import com.example.app_learn_chinese_2025.view.fragment.SearchFragment;
import com.example.app_learn_chinese_2025.view.fragment.TienTrinhFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnLogout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private SessionManager sessionManager;
    private AuthController authController;
    private List<Fragment> fragmentList;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        initViews();
        setupViewPager();
        setupTabLayout();
        loadUserInfo();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        sessionManager = new SessionManager(this);
        authController = new AuthController(this);
        fragmentList = new ArrayList<>();
    }

    private void setupViewPager() {
        // Add fragments
        fragmentList.add(new BaiGiangListFragment());
        fragmentList.add(new TienTrinhFragment());
        fragmentList.add(new SearchFragment());

        // Setup ViewPager
        viewPagerAdapter = new ViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        // Connect TabLayout with ViewPager2
        String[] tabTitles = {"Bài giảng", "Tiến trình", "Tìm kiếm"};

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }

    private void loadUserInfo() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            tvWelcome.setText("Xin chào " + user.getHoTen() + " (Học viên)");
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Process logout
        authController.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}