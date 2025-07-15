package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.controller.TeacherBaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity
        implements BaiGiangController.OnBaiGiangListener,
        BaiGiangAdapter.OnBaiGiangActionListener,
        TeacherBaiGiangController.OnTeacherBaiGiangListener {

    private static final int REQUEST_ADD_BAI_GIANG = 1001;
    private static final int REQUEST_EDIT_BAI_GIANG = 1002;
    private static final String TAG = "TeacherDashboardActivity";

    // UI Components
    private Toolbar toolbar;
    private TextView tvWelcome, tvEmptyState, tvTotalLessons, tvTotalViews, tvPublicLessons, tvPremiumLessons;
    private Button btnLogout, btnManageLessons;
    private CardView cardManageLessons, cardStatistics, cardQuickCreate;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupFilters;
    private SearchView searchView;

    // Data & Controllers
    private SessionManager sessionManager;
    private AuthController authController;
    private BaiGiangController baiGiangController;
    private TeacherBaiGiangController teacherBaiGiangController; // NEW: Teacher-specific controller
    private BaiGiangAdapter adapter;
    private List<BaiGiang> baiGiangList;
    private List<BaiGiang> filteredList;

    // State
    private boolean isGridView = false;
    private String currentSearchQuery = "";
    private int currentFilterType = -1; // -1: All, 0: Video, 1: No Video, 2: Premium

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        // Kiểm tra vai trò giảng viên
        if (sessionManager.getUserRole() != Constants.ROLE_TEACHER) {
            Toast.makeText(this, "Chỉ giảng viên có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_teacher_dashboard);

        initViews();
        setupToolbar();
        setupListeners();
        setupRecyclerView();
        setupFilters();
        loadUserInfo();
        loadBaiGiangs();
        loadQuickStats(); // NEW: Load statistics for dashboard
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        tvTotalViews = findViewById(R.id.tvTotalViews);
        tvPublicLessons = findViewById(R.id.tvPublicLessons);
        tvPremiumLessons = findViewById(R.id.tvPremiumLessons);
        btnLogout = findViewById(R.id.btnLogout);
        btnManageLessons = findViewById(R.id.btnManageLessons);

        // Card views for dashboard sections
        cardManageLessons = findViewById(R.id.cardManageLessons);
        cardStatistics = findViewById(R.id.cardStatistics);
        cardQuickCreate = findViewById(R.id.cardQuickCreate);


        rvBaiGiang = findViewById(R.id.rvBaiGiang);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);

        // Initialize controllers
        authController = new AuthController(this);
        baiGiangController = new BaiGiangController(this, this);
        teacherBaiGiangController = new TeacherBaiGiangController(this, this); // NEW

        baiGiangList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard Giáo viên");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_dashboard, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Tìm kiếm bài giảng...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                performSearch(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_toggle) {
            toggleViewMode();
            return true;
        } else if (id == R.id.action_refresh) {
            loadBaiGiangs();
            loadQuickStats(); // Also refresh stats
            return true;
        } else if (id == R.id.action_manage_lessons) {
            // NEW: Direct navigation to lesson management
            navigateToLessonManagement();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Tính năng cài đặt đang phát triển", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // NEW: Lesson management navigation
        if (btnManageLessons != null) {
            btnManageLessons.setOnClickListener(v -> navigateToLessonManagement());
        }

        if (cardManageLessons != null) {
            cardManageLessons.setOnClickListener(v -> navigateToLessonManagement());
        }


        // Quick create card
        if (cardQuickCreate != null) {
            cardQuickCreate.setOnClickListener(v -> {
                // Navigate to new lesson creation form
                Intent intent = new Intent(TeacherDashboardActivity.this, TeacherCreateEditLessonActivity.class);
                startActivity(intent);
            });
        }

        // Statistics card
        if (cardStatistics != null) {
            cardStatistics.setOnClickListener(v -> {
                // Show detailed statistics or navigate to stats page
                loadQuickStats();
                Toast.makeText(this, "Đang tải thống kê...", Toast.LENGTH_SHORT).show();
            });
        }

        swipeRefresh.setOnRefreshListener(() -> {
            loadBaiGiangs();
            loadQuickStats();
        });
    }

    // NEW: Navigation to Teacher Lesson Management
    private void navigateToLessonManagement() {
        Intent intent = new Intent(TeacherDashboardActivity.this, TeacherLessonManagementActivity.class);
        startActivity(intent);
    }

    // NEW: Load quick statistics for dashboard
    private void loadQuickStats() {
        if (teacherBaiGiangController != null) {
            teacherBaiGiangController.getStatistics();
        }
    }

    // NEW: Update dashboard stats display
    private void updateDashboardStats(ApiService.TeacherBaiGiangResponse.StatsResponse stats) {
        if (stats != null) {
            if (tvTotalLessons != null) {
                tvTotalLessons.setText(String.valueOf(stats.getTongSoBaiGiang()));
            }
            if (tvPublicLessons != null) {
                tvPublicLessons.setText(String.valueOf(stats.getBaiGiangCongKhai()));
            }
            if (tvPremiumLessons != null) {
                tvPremiumLessons.setText(String.valueOf(stats.getBaiGiangGoi()));
            }
            if (tvTotalViews != null) {
                tvTotalViews.setText(String.valueOf(stats.getTongLuotXem()));
            }

            Log.d(TAG, "Updated dashboard stats: " + stats.getTongSoBaiGiang() + " lessons, " +
                    stats.getTongLuotXem() + " views");
        }
    }

    private void setupRecyclerView() {
        adapter = new BaiGiangAdapter(this, filteredList, true, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(this));
        rvBaiGiang.setAdapter(adapter);
    }

    private void setupFilters() {
        Chip chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) chipAll.setOnClickListener(v -> applyFilter(-1));

        Chip chipWithVideo = findViewById(R.id.chipWithVideo);
        if (chipWithVideo != null) chipWithVideo.setOnClickListener(v -> applyFilter(0));

        Chip chipNoVideo = findViewById(R.id.chipNoVideo);
        if (chipNoVideo != null) chipNoVideo.setOnClickListener(v -> applyFilter(1));

        Chip chipPremium = findViewById(R.id.chipPremium);
        if (chipPremium != null) chipPremium.setOnClickListener(v -> applyFilter(2));

        if (chipAll != null) chipAll.setChecked(true);
    }

    private void loadUserInfo() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            tvWelcome.setText("Xin chào " + user.getHoTen() + " (Giáo viên)");
            Log.d(TAG, "User loaded: " + user.getHoTen() + " (ID: " + user.getID() + ")");
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User is null");
            logout();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadBaiGiangs() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            showEmptyState(true, "Không có kết nối mạng. Vui lòng kiểm tra kết nối.");
            return;
        }

        swipeRefresh.setRefreshing(true);
        showEmptyState(false);
        User user = sessionManager.getUserDetails();
        if (user != null) {
            Log.d(TAG, "Loading bài giảng for teacher ID: " + user.getID());
            baiGiangController.getBaiGiangList(user.getID(), null, null, null, null);
        } else {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            logout();
        }
    }

    private void applyFilter(int filterType) {
        currentFilterType = filterType;
        applyCurrentFilters();
    }

    private void performSearch(String query) {
        currentSearchQuery = query.toLowerCase().trim();
        applyCurrentFilters();
    }

    private void applyCurrentFilters() {
        filteredList.clear();
        for (BaiGiang baiGiang : baiGiangList) {
            boolean matchesSearch = true;
            boolean matchesFilter = true;

            // Tìm kiếm theo tiêu đề hoặc mô tả
            if (!currentSearchQuery.isEmpty()) {
                matchesSearch = baiGiang.getTieuDe().toLowerCase().contains(currentSearchQuery) ||
                        (baiGiang.getMoTa() != null && baiGiang.getMoTa().toLowerCase().contains(currentSearchQuery));
            }

            // Lọc theo loại
            switch (currentFilterType) {
                case 0: // Có video
                    matchesFilter = baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty();
                    break;
                case 1: // Không có video
                    matchesFilter = baiGiang.getVideoURL() == null || baiGiang.getVideoURL().isEmpty();
                    break;
                case 2: // Premium
                    matchesFilter = baiGiang.isLaBaiGiangGoi();
                    break;
                default: // Tất cả
                    matchesFilter = true;
                    break;
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(baiGiang);
            }
        }

        adapter.updateData(filteredList);
        if (filteredList.isEmpty() && !baiGiangList.isEmpty()) {
            showEmptyState(true, "Không tìm thấy bài giảng phù hợp");
        } else if (filteredList.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }

        Log.d(TAG, "Applied filters - Total: " + baiGiangList.size() + ", Filtered: " + filteredList.size());
    }

    private void updateStatistics() {
        int totalLessons = baiGiangList.size();
        int totalViews = 0;
        for (BaiGiang baiGiang : baiGiangList) {
            totalViews += baiGiang.getLuotXem();
        }
        if (tvTotalLessons != null) tvTotalLessons.setText(String.valueOf(totalLessons));
        if (tvTotalViews != null) tvTotalViews.setText(String.valueOf(totalViews));
        Log.d(TAG, "Statistics - Lessons: " + totalLessons + ", Views: " + totalViews);
    }

    private void toggleViewMode() {
        isGridView = !isGridView;
        if (isGridView) {
            rvBaiGiang.setLayoutManager(new GridLayoutManager(this, 2));
            Toast.makeText(this, "Chế độ lưới", Toast.LENGTH_SHORT).show();
        } else {
            rvBaiGiang.setLayoutManager(new LinearLayoutManager(this));
            Toast.makeText(this, "Chế độ danh sách", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    private void showEmptyState(boolean show) {
        showEmptyState(show, "Bạn chưa có bài giảng nào. Nhấn nút + để thêm bài giảng mới.");
    }

    private void showEmptyState(boolean show, String message) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            tvEmptyState.setText(message);
        }
        rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        authController.logout();
        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ===== BaiGiangController.OnBaiGiangListener implementations =====
    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        this.baiGiangList = baiGiangList;
        applyCurrentFilters();
        updateStatistics();
        swipeRefresh.setRefreshing(false);
        if (baiGiangList.isEmpty()) {
            showEmptyState(true);
        }
        Log.d(TAG, "Received " + baiGiangList.size() + " bài giảng");
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        // Not used in dashboard
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        loadBaiGiangs();
        loadQuickStats();
        Toast.makeText(this, "Tạo bài giảng thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        loadBaiGiangs();
        loadQuickStats();
        Toast.makeText(this, "Cập nhật bài giảng thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBaiGiangListReceived(ApiService.TeacherBaiGiangResponse.PageResponse response) {

    }

    @Override
    public void onBaiGiangDetailReceived(ApiService.TeacherBaiGiangResponse.DetailResponse baiGiang) {

    }

    @Override
    public void onBaiGiangCreated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {

    }

    @Override
    public void onBaiGiangUpdated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {

    }

    @Override
    public void onBaiGiangDeleted() {
        loadBaiGiangs();
        loadQuickStats();
        Toast.makeText(this, "Xóa bài giảng thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusToggled(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {

    }

    @Override
    public void onBaiGiangDuplicated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {

    }

    @Override
    public void onStatisticsReceived(ApiService.TeacherBaiGiangResponse.StatsResponse stats) {

    }

    @Override
    public void onSearchResultReceived(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> results) {

    }

    @Override
    public void onError(String message) {
        swipeRefresh.setRefreshing(false);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        showEmptyState(true, "Lỗi tải dữ liệu: " + message);
        Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onItemClick(BaiGiang baiGiang) {

    }

    @Override
    public void onEditBaiGiang(BaiGiang baiGiang) {

    }

    @Override
    public void onDeleteBaiGiang(BaiGiang baiGiang) {

    }

    @Override
    public void onPlayVideo(BaiGiang baiGiang) {

    }

    @Override
    public void onPlayAudio(BaiGiang baiGiang) {

    }
}

