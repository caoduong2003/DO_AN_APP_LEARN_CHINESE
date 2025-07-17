package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.ViewPagerAdapter;
import com.example.app_learn_chinese_2025.view.fragment.StudentBaiGiangListFragment;
import com.example.app_learn_chinese_2025.view.fragment.StudentProgressFragment;
import com.example.app_learn_chinese_2025.view.fragment.StudentProfileFragment;
import com.example.app_learn_chinese_2025.view.fragment.VisualLearningFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.app_learn_chinese_2025.view.fragment.StudentExerciseFragment;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "STUDENT_DASHBOARD";

    // UI Components
    private Toolbar toolbar;
    private TextView tvWelcome;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigation;

    // Data & Controllers
    private SessionManager sessionManager;
    private AuthController authController;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragmentList;

    // Fragments
    private StudentBaiGiangListFragment baiGiangListFragment;
    private StudentExerciseFragment exerciseFragment;
    private VisualLearningFragment visualLearningFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_dashboard);
            Log.d(TAG, "StudentDashboardActivity onCreate started");

            initViews();
            setupToolbar();
            loadUserInfo();
            setupViewPager();
            setupBottomNavigation();

            Log.d(TAG, "StudentDashboardActivity onCreate completed");
        } catch (Exception e) {
            Log.e(TAG, "Crash in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            redirectToLogin();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        sessionManager = new SessionManager(this);
        authController = new AuthController(this);
        fragmentList = new ArrayList<>();

        Log.d(TAG, "Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Học tiếng Trung");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        Log.d(TAG, "Toolbar setup completed");
    }

    private void loadUserInfo() {
        try {
            User user = sessionManager.getUserDetails();
            if (user != null && user.getHoTen() != null && !user.getHoTen().isEmpty()) {
                String welcomeText = "Xin chào " + user.getHoTen() + "! 👋";
                tvWelcome.setText(welcomeText);
                Log.d(TAG, "User loaded: " + user.getHoTen() + " (ID: " + user.getID() + ")");
            } else {
                tvWelcome.setText("Xin chào học viên!");
                Log.w(TAG, "User is null or HoTen is invalid");
                redirectToLogin();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user info: " + e.getMessage(), e);
            tvWelcome.setText("Xin chào học viên!");
            redirectToLogin();
        }
    }

    private void setupViewPager() {
        // Tạo các fragments cho học viên
        baiGiangListFragment = new StudentBaiGiangListFragment();
        exerciseFragment = new StudentExerciseFragment();
        visualLearningFragment = new VisualLearningFragment();

        // Thêm vào list
        fragmentList.add(baiGiangListFragment);
        fragmentList.add(exerciseFragment);
        fragmentList.add(visualLearningFragment);

        // Setup adapter
        viewPagerAdapter = new ViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        // Connect TabLayout với ViewPager2
        String[] tabTitles = {"Bài giảng", "Bài tập", "Học từ vựng qua hình ảnh"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);

            // Thêm icon cho tab
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_school);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_quiz);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_camera);
                    break;
            }
        }).attach();

        // Set default tab
        viewPager.setCurrentItem(0);

        Log.d(TAG, "ViewPager setup completed with " + fragmentList.size() + " fragments");
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_lessons) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (id == R.id.nav_exercises) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (id == R.id.nav_visual_learning) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            });

            // Sync với ViewPager
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    switch (position) {
                        case 0:
                            bottomNavigation.setSelectedItemId(R.id.nav_lessons);
                            break;
                        case 1:
                            bottomNavigation.setSelectedItemId(R.id.nav_exercises);
                            break;
                        case 2:
                            bottomNavigation.setSelectedItemId(R.id.nav_visual_learning);
                            break;
                    }
                }
            });
        }

        Log.d(TAG, "Bottom navigation setup completed");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshCurrentFragment();
            return true;
        } else if (id == R.id.nav_visual_learning) {
            viewPager.setCurrentItem(2);
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Tính năng cài đặt đang phát triển", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshCurrentFragment() {
        int currentItem = viewPager.getCurrentItem();

        Log.d(TAG, "Refreshing fragment at position: " + currentItem);

        if (currentItem < 0 || currentItem >= fragmentList.size()) {
            Log.w(TAG, "Invalid fragment position: " + currentItem);
            Toast.makeText(this, "Lỗi làm mới", Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment currentFragment = fragmentList.get(currentItem);

        try {
            if (currentFragment instanceof StudentBaiGiangListFragment) {
                Log.d(TAG, "Refreshing StudentBaiGiangListFragment");
                ((StudentBaiGiangListFragment) currentFragment).refreshData();
                Toast.makeText(this, "Đã làm mới danh sách bài giảng", Toast.LENGTH_SHORT).show();

            } else if (currentFragment instanceof StudentExerciseFragment) {
                Log.d(TAG, "Refreshing StudentExerciseFragment");
                ((StudentExerciseFragment) currentFragment).refreshData();
                Toast.makeText(this, "Đã làm mới bài tập", Toast.LENGTH_SHORT).show();

            } else if (currentFragment instanceof VisualLearningFragment) {
                Log.d(TAG, "Refreshing VisualLearningFragment");

                // ✅ FIX: Thực sự gọi refresh method của fragment
                VisualLearningFragment vlFragment = (VisualLearningFragment) currentFragment;

                // Kiểm tra fragment có sẵn sàng không
                if (vlFragment.isAdded() && !vlFragment.isDetached() && vlFragment.getView() != null) {
                    // Gọi method refresh
                    vlFragment.refreshData();
                    Toast.makeText(this, "Đã làm mới camera", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "VisualLearningFragment refreshed successfully");
                } else {
                    Log.w(TAG, "VisualLearningFragment not ready for refresh");
                    Toast.makeText(this, "Camera chưa sẵn sàng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }

            } else if (currentFragment instanceof StudentProgressFragment) {
                Log.d(TAG, "Refreshing StudentProgressFragment");
                ((StudentProgressFragment) currentFragment).refreshData();
                Toast.makeText(this, "Đã làm mới tiến trình", Toast.LENGTH_SHORT).show();

            } else {
                Log.w(TAG, "Unknown fragment type: " + currentFragment.getClass().getSimpleName());
                Toast.makeText(this, "Đã làm mới", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error refreshing fragment: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi làm mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "Refresh completed for position: " + currentItem);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    private void logout() {
        Log.d(TAG, "User logout initiated");

        authController.logout();
        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Log.d(TAG, "Redirected to login");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verify user is still logged in
        User user = sessionManager.getUserDetails();
        if (user == null) {
            Log.w(TAG, "User session expired, redirecting to login");
            redirectToLogin();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "StudentDashboardActivity destroyed");
    }

    // Public methods để access từ fragments
    public void navigateToLessonDetail(long lessonId) {
        Intent intent = new Intent(this, BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", lessonId);
        startActivity(intent);
    }

    public void showProgressTab() {
        viewPager.setCurrentItem(1);
    }

    public void showProfileTab() {
        viewPager.setCurrentItem(2);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    // Handle back press
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // Nếu đang ở tab đầu tiên, hiển thị dialog thoát
            new AlertDialog.Builder(this)
                    .setTitle("Thoát ứng dụng")
                    .setMessage("Bạn có muốn thoát ứng dụng?")
                    .setPositiveButton("Thoát", (dialog, which) -> {
                        super.onBackPressed();
                        finishAffinity(); // Đóng hoàn toàn app
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            // Nếu không ở tab đầu, quay về tab đầu tiên
            viewPager.setCurrentItem(0);
        }
    }
}