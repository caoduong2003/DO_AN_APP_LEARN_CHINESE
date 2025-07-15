package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.TeacherBaiGiangController;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.TeacherBaiGiangService;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.TeacherLessonAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 Activity quản lý bài giảng của giáo viên
 * Hiển thị danh sách, thêm/sửa/xóa bài giảng
 */
public class TeacherLessonManagementActivity extends AppCompatActivity
        implements TeacherBaiGiangController.OnTeacherBaiGiangListener,
        TeacherLessonAdapter.OnLessonActionListener {

    private static final String TAG = "TeacherLessonManagement";
    private static final int REQUEST_ADD_LESSON = 1001;
    private static final int REQUEST_EDIT_LESSON = 1002;

    // UI Components
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvLessons;
    private TextView tvEmptyMessage, tvStats;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;

    // Data & Controllers
    private SessionManager sessionManager;
    private TeacherBaiGiangController controller;
    private TeacherLessonAdapter adapter;
    private List<ApiService.TeacherBaiGiangResponse.SimpleResponse> lessonList;

    // Pagination
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    // Search
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_lesson_management);

        Log.d(TAG, "🚀 onCreate started");

        // Kiểm tra quyền giáo viên
        sessionManager = new SessionManager(this);
        if (sessionManager.getUserRole() != com.example.app_learn_chinese_2025.util.Constants.ROLE_TEACHER) {
            Toast.makeText(this, "Chỉ giảng viên có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadLessons();
        loadStatistics();
    }

    // ===== INITIALIZATION METHODS =====

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvLessons = findViewById(R.id.rvLessons);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tvStats = findViewById(R.id.tvStats);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);

        // Initialize controllers
        controller = new TeacherBaiGiangController(this, this);
        lessonList = new ArrayList<>();

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý bài giảng");
        }
    }

    private void setupRecyclerView() {
        adapter = new TeacherLessonAdapter(this, lessonList, this);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        rvLessons.setAdapter(adapter);

        // Setup pagination
        rvLessons.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                        loadMoreLessons();
                    }
                }
            }
        });

        Log.d(TAG, "✅ RecyclerView setup completed");
    }

    private void setupListeners() {
        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> {
            currentPage = 0;
            hasMoreData = true;
            loadLessons();
        });

        // FAB click
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeacherCreateEditLessonActivity.class);
            startActivityForResult(intent, REQUEST_ADD_LESSON);
        });

        Log.d(TAG, "✅ Listeners setup completed");
    }

// ===== MENU HANDLING =====

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_lesson_management, menu);

        // Setup search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Tìm kiếm bài giảng...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchLessons(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.length() == 0) {
                            currentSearchQuery = "";
                            currentPage = 0;
                            loadLessons();
                        }
                        return true;
                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_refresh) {
            refreshData();
            return true;
        } else if (itemId == R.id.action_statistics) {
            loadStatistics();
            return true;
        } else if (itemId == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (itemId == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (itemId == R.id.action_export) {
            exportLessons();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

// ===== ADDITIONAL MENU ACTION METHODS =====

    private void showFilterDialog() {
        // Create filter options
        String[] filterOptions = {
                "Tất cả bài giảng",
                "Chỉ bài công khai",
                "Chỉ bài ẩn",
                "Chỉ bài Premium",
                "Chỉ bài miễn phí"
        };

        new AlertDialog.Builder(this)
                .setTitle("Lọc bài giảng")
                .setItems(filterOptions, (dialog, which) -> {
                    applyFilter(which);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showSortDialog() {
        // Create sort options
        String[] sortOptions = {
                "Mới nhất",
                "Cũ nhất",
                "Nhiều lượt xem nhất",
                "Ít lượt xem nhất",
                "Theo tên A-Z",
                "Theo tên Z-A"
        };

        new AlertDialog.Builder(this)
                .setTitle("Sắp xếp bài giảng")
                .setItems(sortOptions, (dialog, which) -> {
                    applySorting(which);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void exportLessons() {
        if (lessonList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xuất dữ liệu")
                .setMessage("Xuất danh sách " + lessonList.size() + " bài giảng?")
                .setPositiveButton("Xuất", (dialog, which) -> {
                    performExport();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void applyFilter(int filterType) {
        currentPage = 0;
        hasMoreData = true;

        Boolean trangThaiFilter = null;
        Boolean premiumFilter = null;

        switch (filterType) {
            case 0: // Tất cả
                trangThaiFilter = null;
                premiumFilter = null;
                break;
            case 1: // Công khai
                trangThaiFilter = true;
                break;
            case 2: // Ẩn
                trangThaiFilter = false;
                break;
            case 3: // Premium
                premiumFilter = true;
                break;
            case 4: // Miễn phí
                premiumFilter = false;
                break;
        }

        // Clear current list
        lessonList.clear();
        adapter.notifyDataSetChanged();

        // Load with new filter
        controller.getMyBaiGiangs(currentPage, 10, "ngayTao", "desc",
                currentSearchQuery.isEmpty() ? null : currentSearchQuery,
                null, null, null, trangThaiFilter);

        // Show filter applied message
        Toast.makeText(this, "Áp dụng bộ lọc: " + getFilterName(filterType), Toast.LENGTH_SHORT).show();
    }

    private void applySorting(int sortType) {
        currentPage = 0;
        hasMoreData = true;

        String sortBy = "ngayTao";
        String sortDir = "desc";

        switch (sortType) {
            case 0: // Mới nhất
                sortBy = "ngayTao";
                sortDir = "desc";
                break;
            case 1: // Cũ nhất
                sortBy = "ngayTao";
                sortDir = "asc";
                break;
            case 2: // Nhiều lượt xem
                sortBy = "luotXem";
                sortDir = "desc";
                break;
            case 3: // Ít lượt xem
                sortBy = "luotXem";
                sortDir = "asc";
                break;
            case 4: // Tên A-Z
                sortBy = "tieuDe";
                sortDir = "asc";
                break;
            case 5: // Tên Z-A
                sortBy = "tieuDe";
                sortDir = "desc";
                break;
        }

        // Clear current list
        lessonList.clear();
        adapter.notifyDataSetChanged();

        // Load with new sorting
        controller.getMyBaiGiangs(currentPage, 10, sortBy, sortDir,
                currentSearchQuery.isEmpty() ? null : currentSearchQuery,
                null, null, null, null);

        // Show sort applied message
        Toast.makeText(this, "Sắp xếp: " + getSortName(sortType), Toast.LENGTH_SHORT).show();
    }

    private void performExport() {
        // Simple text export - you can enhance this to CSV/Excel
        StringBuilder exportData = new StringBuilder();
        exportData.append("DANH SÁCH BÀI GIẢNG\n");
        exportData.append("===================\n\n");

        for (int i = 0; i < lessonList.size(); i++) {
            ApiService.TeacherBaiGiangResponse.SimpleResponse lesson = lessonList.get(i);
            exportData.append((i + 1)).append(". ").append(lesson.getTieuDe()).append("\n");
            exportData.append("   Mô tả: ").append(lesson.getMoTa() != null ? lesson.getMoTa() : "Không có").append("\n");
            exportData.append("   Lượt xem: ").append(lesson.getLuotXem() != null ? lesson.getLuotXem() : 0).append("\n");
            exportData.append("   Trạng thái: ").append(lesson.getTrangThai() ? "Công khai" : "Ẩn").append("\n");
            exportData.append("   Premium: ").append(lesson.getLaBaiGiangGoi() ? "Có" : "Không").append("\n\n");
        }

        // Share the export data
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, exportData.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Danh sách bài giảng");
        startActivity(Intent.createChooser(shareIntent, "Xuất danh sách bài giảng"));

        Toast.makeText(this, "Đã xuất dữ liệu", Toast.LENGTH_SHORT).show();
    }

    private String getFilterName(int filterType) {
        switch (filterType) {
            case 0: return "Tất cả bài giảng";
            case 1: return "Bài công khai";
            case 2: return "Bài ẩn";
            case 3: return "Bài Premium";
            case 4: return "Bài miễn phí";
            default: return "Không xác định";
        }
    }

    private String getSortName(int sortType) {
        switch (sortType) {
            case 0: return "Mới nhất";
            case 1: return "Cũ nhất";
            case 2: return "Nhiều lượt xem nhất";
            case 3: return "Ít lượt xem nhất";
            case 4: return "Tên A-Z";
            case 5: return "Tên Z-A";
            default: return "Không xác định";
        }
    }

    // ===== DATA LOADING METHODS =====

    private void loadLessons() {
        Log.d(TAG, "🌐 Loading lessons - page: " + currentPage);

        if (currentPage == 0) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
        }
        isLoading = true;

        controller.getMyBaiGiangs(currentPage, 10, "ngayTao", "desc",
                currentSearchQuery.isEmpty() ? null : currentSearchQuery,
                null, null, null, null);
    }

    private void loadMoreLessons() {
        if (hasMoreData && !isLoading) {
            currentPage++;
            loadLessons();
        }
    }

    private void searchLessons(String query) {
        currentSearchQuery = query;
        currentPage = 0;
        hasMoreData = true;
        lessonList.clear();
        adapter.notifyDataSetChanged();
        loadLessons();
    }

    private void refreshData() {
        currentPage = 0;
        hasMoreData = true;
        currentSearchQuery = "";
        loadLessons();
        loadStatistics();
    }

    private void loadStatistics() {
        controller.getStatistics();
    }

    // ===== UI HELPER METHODS =====

    private void showEmptyState(boolean show, String message) {
        tvEmptyMessage.setVisibility(show ? View.VISIBLE : View.GONE);
        rvLessons.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show && message != null) {
            tvEmptyMessage.setText(message);
        }
    }

    private void updateStats(ApiService.TeacherBaiGiangResponse.StatsResponse stats) {
        if (stats != null && tvStats != null) {
            String statsText = String.format("Tổng: %d | Công khai: %d | Premium: %d | Lượt xem: %d",
                    stats.getTongSoBaiGiang(),
                    stats.getBaiGiangCongKhai(),
                    stats.getBaiGiangGoi(),
                    stats.getTongLuotXem());
            tvStats.setText(statsText);
            tvStats.setVisibility(View.VISIBLE);
        }
    }

    // ===== IMPLEMENT TeacherBaiGiangController.OnTeacherBaiGiangListener =====

    @Override
    public void onBaiGiangListReceived(ApiService.TeacherBaiGiangResponse.PageResponse response) {
        Log.d(TAG, "✅ Received lesson list with " + response.getContent().size() + " items");

        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);

        if (currentPage == 0) {
            lessonList.clear();
        }

        lessonList.addAll(response.getContent());
        adapter.notifyDataSetChanged();

        hasMoreData = !response.isLast();

        if (lessonList.isEmpty()) {
            showEmptyState(true, currentSearchQuery.isEmpty() ?
                    "Chưa có bài giảng nào" : "Không tìm thấy bài giảng phù hợp");
        } else {
            showEmptyState(false, null);
        }
    }

    @Override
    public void onBaiGiangDetailReceived(ApiService.TeacherBaiGiangResponse.DetailResponse baiGiang) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangCreated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Lesson created: " + baiGiang.getTieuDe());
        Toast.makeText(this, "Tạo bài giảng thành công", Toast.LENGTH_SHORT).show();
        refreshData();
    }

    @Override
    public void onBaiGiangUpdated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Lesson updated: " + baiGiang.getTieuDe());
        Toast.makeText(this, "Cập nhật bài giảng thành công", Toast.LENGTH_SHORT).show();
        refreshData();
    }



    @Override
    public void onBaiGiangDeleted() {
        Log.d(TAG, "✅ Lesson deleted");
        Toast.makeText(this, "Xóa bài giảng thành công", Toast.LENGTH_SHORT).show();
        refreshData();
    }



    @Override
    public void onStatusToggled(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Status toggled for: " + baiGiang.getTieuDe());
        Toast.makeText(this, "Thay đổi trạng thái thành công", Toast.LENGTH_SHORT).show();
        refreshData();
    }

    @Override
    public void onBaiGiangDuplicated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Lesson duplicated: " + baiGiang.getTieuDe());
        Toast.makeText(this, "Nhân bản bài giảng thành công", Toast.LENGTH_SHORT).show();
        refreshData();
    }

    @Override
    public void onStatisticsReceived(ApiService.TeacherBaiGiangResponse.StatsResponse stats) {
        Log.d(TAG, "✅ Statistics received");
        updateStats(stats);
    }

    @Override
    public void onSearchResultReceived(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> results) {
        Log.d(TAG, "✅ Search results received: " + results.size() + " items");
        // Handle search results if needed
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "❌ Error: " + message);

        isLoading = false;
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);

        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();

        if (lessonList.isEmpty()) {
            showEmptyState(true, "Lỗi tải dữ liệu: " + message);
        }
    }

    // ===== IMPLEMENT TeacherLessonAdapter.OnLessonActionListener =====

    @Override
    public void onLessonClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        // View lesson detail
        Intent intent = new Intent(this, TeacherLessonDetailActivity.class);
        intent.putExtra("LESSON_ID", lesson.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        Intent intent = new Intent(this, TeacherCreateEditLessonActivity.class);
        intent.putExtra("LESSON_ID", lesson.getId());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivityForResult(intent, REQUEST_EDIT_LESSON);
    }

    @Override
    public void onDeleteClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài giảng")
                .setMessage("Bạn có chắc chắn muốn xóa bài giảng \"" + lesson.getTieuDe() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    controller.deleteBaiGiang(lesson.getId());
                })
                .setNegativeButton("Hủy", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    @Override
    public void onToggleStatusClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        String action = lesson.getTrangThai() ? "ẩn" : "công khai";
        new AlertDialog.Builder(this)
                .setTitle("Thay đổi trạng thái")
                .setMessage("Bạn có muốn " + action + " bài giảng \"" + lesson.getTieuDe() + "\"?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    controller.toggleStatus(lesson.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onTogglePremiumClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        String action = lesson.getLaBaiGiangGoi() ? "miễn phí" : "premium";
        new AlertDialog.Builder(this)
                .setTitle("Thay đổi loại bài giảng")
                .setMessage("Bạn có muốn chuyển bài giảng \"" + lesson.getTieuDe() + "\" thành " + action + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    controller.togglePremium(lesson.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDuplicateClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
        // Show dialog to enter new title
        android.widget.EditText editText = new android.widget.EditText(this);
        editText.setHint("Tiêu đề mới (tùy chọn)");

        new AlertDialog.Builder(this)
                .setTitle("Nhân bản bài giảng")
                .setMessage("Nhân bản bài giảng \"" + lesson.getTieuDe() + "\"")
                .setView(editText)
                .setPositiveButton("Nhân bản", (dialog, which) -> {
                    String newTitle = editText.getText().toString().trim();
                    controller.duplicateBaiGiang(lesson.getId(), newTitle.isEmpty() ? null : newTitle);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ===== ACTIVITY RESULT =====

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_LESSON || requestCode == REQUEST_EDIT_LESSON) {
                refreshData();
            }
        }
    }

    // ===== LIFECYCLE =====

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (lessonList.size() > 0) {
            loadStatistics(); // Only refresh stats, not the whole list
        }
    }
}