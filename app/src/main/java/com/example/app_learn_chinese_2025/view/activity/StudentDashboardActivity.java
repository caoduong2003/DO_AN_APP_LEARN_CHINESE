package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "StudentDashboard";

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

        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_student_dashboard);
            Log.d(TAG, "Layout set successfully");

            initViews();
            Log.d(TAG, "Views initialized");

            setupViewPager();
            Log.d(TAG, "ViewPager setup");

            setupTabLayout();
            Log.d(TAG, "TabLayout setup");

            loadUserInfo();
            Log.d(TAG, "User info loaded");

            setupListeners();
            Log.d(TAG, "Listeners setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            Log.d(TAG, "Finding views...");

            tvWelcome = findViewById(R.id.tvWelcome);
            if (tvWelcome == null) {
                throw new RuntimeException("tvWelcome not found");
            }

            btnLogout = findViewById(R.id.btnLogout);
            if (btnLogout == null) {
                throw new RuntimeException("btnLogout not found");
            }

            tabLayout = findViewById(R.id.tabLayout);
            if (tabLayout == null) {
                throw new RuntimeException("tabLayout not found");
            }

            viewPager = findViewById(R.id.viewPager);
            if (viewPager == null) {
                throw new RuntimeException("viewPager not found");
            }

            Log.d(TAG, "All views found successfully");

            sessionManager = new SessionManager(this);
            authController = new AuthController(this);
            fragmentList = new ArrayList<>();

            Log.d(TAG, "Objects initialized");

        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupViewPager() {
        try {
            Log.d(TAG, "Setting up ViewPager...");

            // Tạo từng fragment một cách an toàn
            try {
                BaiGiangListFragment baiGiangFragment = new BaiGiangListFragment();
                fragmentList.add(baiGiangFragment);
                Log.d(TAG, "BaiGiangListFragment added");
            } catch (Exception e) {
                Log.e(TAG, "Error creating BaiGiangListFragment: " + e.getMessage());
                throw new RuntimeException("Cannot create BaiGiangListFragment");
            }

            try {
                TienTrinhFragment tienTrinhFragment = new TienTrinhFragment();
                fragmentList.add(tienTrinhFragment);
                Log.d(TAG, "TienTrinhFragment added");
            } catch (Exception e) {
                Log.e(TAG, "Error creating TienTrinhFragment: " + e.getMessage());
                throw new RuntimeException("Cannot create TienTrinhFragment");
            }

            try {
                SearchFragment searchFragment = new SearchFragment();
                fragmentList.add(searchFragment);
                Log.d(TAG, "SearchFragment added");
            } catch (Exception e) {
                Log.e(TAG, "Error creating SearchFragment: " + e.getMessage());
                throw new RuntimeException("Cannot create SearchFragment");
            }

            Log.d(TAG, "Total fragments: " + fragmentList.size());

            // Setup ViewPager
            viewPagerAdapter = new ViewPagerAdapter(this, fragmentList);
            viewPager.setAdapter(viewPagerAdapter);

            Log.d(TAG, "ViewPager adapter set");

        } catch (Exception e) {
            Log.e(TAG, "Error in setupViewPager: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupTabLayout() {
        try {
            Log.d(TAG, "Setting up TabLayout...");

            String[] tabTitles = {"Bài giảng", "Tiến trình", "Tìm kiếm"};

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position < tabTitles.length) {
                    tab.setText(tabTitles[position]);
                    Log.d(TAG, "Tab " + position + " set: " + tabTitles[position]);
                }
            }).attach();

            Log.d(TAG, "TabLayoutMediator attached");

        } catch (Exception e) {
            Log.e(TAG, "Error in setupTabLayout: " + e.getMessage(), e);
            throw e;
        }
    }

    private void loadUserInfo() {
        try {
            Log.d(TAG, "Loading user info...");

            User user = sessionManager.getUserDetails();
            if (user != null) {
                Log.d(TAG, "User found: " + user.getHoTen());

                String welcomeMessage = "Xin chào " + user.getHoTen();
                if (user.getTrinhDoHSK() > 0) {
                    welcomeMessage += " (HSK " + user.getTrinhDoHSK() + ")";
                } else {
                    welcomeMessage += " (Học viên)";
                }
                tvWelcome.setText(welcomeMessage);

                Log.d(TAG, "Welcome message set: " + welcomeMessage);
            } else {
                Log.w(TAG, "User is null");
                tvWelcome.setText("Xin chào học viên");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in loadUserInfo: " + e.getMessage(), e);
            tvWelcome.setText("Xin chào học viên");
        }
    }

    private void setupListeners() {
        try {
            Log.d(TAG, "Setting up listeners...");

            btnLogout.setOnClickListener(v -> {
                Log.d(TAG, "Logout button clicked");
                logout();
            });

            Log.d(TAG, "Listeners set successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in setupListeners: " + e.getMessage(), e);
            throw e;
        }
    }

    private void logout() {
        try {
            Log.d(TAG, "Logout process started");

            authController.logout();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Log.d(TAG, "Logout completed");

        } catch (Exception e) {
            Log.e(TAG, "Error in logout: " + e.getMessage(), e);
            Toast.makeText(this, "Có lỗi khi đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume called");
            loadUserInfo();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
    }
}