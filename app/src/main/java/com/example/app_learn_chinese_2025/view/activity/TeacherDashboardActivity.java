package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherDashboardActivity extends AppCompatActivity implements BaiGiangAdapter.OnBaiGiangItemClickListener {
    private static final int REQUEST_ADD_BAI_GIANG = 1001;
    private static final int REQUEST_EDIT_BAI_GIANG = 1002;

    // UI Components
    private Toolbar toolbar;
    private TextView tvWelcome, tvEmptyState, tvTotalLessons, tvTotalViews;
    private Button btnLogout;
    private FloatingActionButton fabAddBaiGiang;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupFilters;
    private SearchView searchView;

    // Data & Controllers
    private SessionManager sessionManager;
    private AuthController authController;
    private BaiGiangRepository baiGiangRepository;
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
        setContentView(R.layout.activity_teacher_dashboard);

        initViews();
        setupToolbar();
        setupListeners();
        setupRecyclerView();
        setupFilters();
        loadUserInfo();
        loadBaiGiangs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        tvTotalViews = findViewById(R.id.tvTotalViews);
        btnLogout = findViewById(R.id.btnLogout);
        fabAddBaiGiang = findViewById(R.id.fabAddBaiGiang);
        rvBaiGiang = findViewById(R.id.rvBaiGiang);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);

        sessionManager = new SessionManager(this);
        authController = new AuthController(this);
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        baiGiangList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý bài giảng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_dashboard, menu);

        // Setup SearchView
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
            return true;
        } else if (id == R.id.action_settings) {
            // TODO: Navigate to settings
            Toast.makeText(this, "Tính năng cài đặt đang phát triển", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        fabAddBaiGiang.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, EditBaiGiangActivity.class);
            startActivityForResult(intent, REQUEST_ADD_BAI_GIANG);
        });

        swipeRefresh.setOnRefreshListener(this::loadBaiGiangs);
    }

    private void setupRecyclerView() {
        adapter = new BaiGiangAdapter(this, filteredList, true, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(this));
        rvBaiGiang.setAdapter(adapter);
    }

    private void setupFilters() {
        // Filter: Tất cả
        Chip chipAll = findViewById(R.id.chipAll);
        chipAll.setOnClickListener(v -> applyFilter(-1));

        // Filter: Có video
        Chip chipWithVideo = findViewById(R.id.chipWithVideo);
        chipWithVideo.setOnClickListener(v -> applyFilter(0));

        // Filter: Không có video
        Chip chipNoVideo = findViewById(R.id.chipNoVideo);
        chipNoVideo.setOnClickListener(v -> applyFilter(1));

        // Filter: Premium
        Chip chipPremium = findViewById(R.id.chipPremium);
        chipPremium.setOnClickListener(v -> applyFilter(2));

        // Set default filter
        chipAll.setChecked(true);
    }

    private void loadUserInfo() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            tvWelcome.setText("Xin chào " + user.getHoTen() + " (Giáo viên)");
            Log.d("TEACHER_DASHBOARD", "User loaded: " + user.getHoTen() + " (ID: " + user.getID() + ")");
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            Log.e("TEACHER_DASHBOARD", "User is null");
        }
    }

    private void loadBaiGiangs() {
        swipeRefresh.setRefreshing(true);
        showEmptyState(false);

        User user = sessionManager.getUserDetails();
        if (user != null) {
            Log.d("TEACHER_DASHBOARD", "Loading bài giảng for teacher ID: " + user.getID());

            baiGiangRepository.getBaiGiangByGiangVien(user.getID(), new BaiGiangRepository.OnBaiGiangListCallback() {
                @Override
                public void onSuccess(List<BaiGiang> baiGiangList) {
                    Log.d("TEACHER_DASHBOARD", "Loaded " + baiGiangList.size() + " bài giảng");

                    TeacherDashboardActivity.this.baiGiangList = baiGiangList;
                    applyCurrentFilters();
                    updateStatistics();
                    swipeRefresh.setRefreshing(false);

                    if (baiGiangList.isEmpty()) {
                        showEmptyState(true);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("TEACHER_DASHBOARD", "Error loading bài giảng: " + errorMessage);
                    Toast.makeText(TeacherDashboardActivity.this, "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_LONG).show();
                    swipeRefresh.setRefreshing(false);
                    showEmptyState(true);
                }
            });
        } else {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
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

            // Apply search filter
            if (!currentSearchQuery.isEmpty()) {
                matchesSearch = baiGiang.getTieuDe().toLowerCase().contains(currentSearchQuery) ||
                        (baiGiang.getMoTa() != null && baiGiang.getMoTa().toLowerCase().contains(currentSearchQuery));
            }

            // Apply type filter
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

        // Show/hide empty state
        if (filteredList.isEmpty() && !baiGiangList.isEmpty()) {
            showEmptyState(true, "Không tìm thấy bài giảng phù hợp");
        } else if (filteredList.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }

        Log.d("TEACHER_DASHBOARD", "Applied filters - Total: " + baiGiangList.size() + ", Filtered: " + filteredList.size());
    }

    private void updateStatistics() {
        int totalLessons = baiGiangList.size();
        int totalViews = 0;

        for (BaiGiang baiGiang : baiGiangList) {
            totalViews += baiGiang.getLuotXem();
        }

        if (tvTotalLessons != null) {
            tvTotalLessons.setText(String.valueOf(totalLessons));
        }

        if (tvTotalViews != null) {
            tvTotalViews.setText(String.valueOf(totalViews));
        }

        Log.d("TEACHER_DASHBOARD", "Statistics - Lessons: " + totalLessons + ", Views: " + totalViews);
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

        Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // BaiGiangAdapter.OnBaiGiangItemClickListener implementations
    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d("TEACHER_DASHBOARD", "Clicked on bài giảng: " + baiGiang.getTieuDe());

        Intent intent = new Intent(TeacherDashboardActivity.this, BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        Log.d("TEACHER_DASHBOARD", "Edit bài giảng: " + baiGiang.getTieuDe());

        Intent intent = new Intent(TeacherDashboardActivity.this, EditBaiGiangActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivityForResult(intent, REQUEST_EDIT_BAI_GIANG);
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        Log.d("TEACHER_DASHBOARD", "Delete bài giảng: " + baiGiang.getTieuDe());

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài giảng \"" + baiGiang.getTieuDe() + "\"?\n\nThao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteBaiGiang(baiGiang))
                .setNegativeButton("Hủy", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    private void deleteBaiGiang(BaiGiang baiGiang) {
        // Show progress
        swipeRefresh.setRefreshing(true);

        baiGiangRepository.deleteBaiGiang(baiGiang.getID(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful()) {
                    Toast.makeText(TeacherDashboardActivity.this, "Xóa bài giảng thành công", Toast.LENGTH_SHORT).show();

                    // Remove from local list to avoid full reload
                    baiGiangList.remove(baiGiang);
                    applyCurrentFilters();
                    updateStatistics();

                    Log.d("TEACHER_DASHBOARD", "Successfully deleted bài giảng: " + baiGiang.getTieuDe());
                } else {
                    Toast.makeText(TeacherDashboardActivity.this, "Xóa bài giảng thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("TEACHER_DASHBOARD", "Failed to delete bài giảng: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(TeacherDashboardActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TEACHER_DASHBOARD", "Error deleting bài giảng: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_BAI_GIANG) {
                loadBaiGiangs(); // Reload để lấy bài giảng mới
                Toast.makeText(this, "Thêm bài giảng thành công", Toast.LENGTH_SHORT).show();
                Log.d("TEACHER_DASHBOARD", "Added new bài giảng");
            } else if (requestCode == REQUEST_EDIT_BAI_GIANG) {
                loadBaiGiangs(); // Reload để cập nhật thay đổi
                Toast.makeText(this, "Cập nhật bài giảng thành công", Toast.LENGTH_SHORT).show();
                Log.d("TEACHER_DASHBOARD", "Updated bài giảng");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (adapter != null && !baiGiangList.isEmpty()) {
            applyCurrentFilters();
        }
    }
}