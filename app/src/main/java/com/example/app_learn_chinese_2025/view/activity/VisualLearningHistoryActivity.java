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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.data.VisualLearning;
import com.example.app_learn_chinese_2025.model.repository.VisualLearningRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.VisualLearningAdapter;

import java.util.List;

public class VisualLearningHistoryActivity extends AppCompatActivity {
    private static final String TAG = "VisualLearningHistory";

    // UI Components
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SearchView searchView;

    // Data & Controllers
    private VisualLearningRepository repository;
    private SessionManager sessionManager;
    private VisualLearningAdapter adapter;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_learning_history);

        Log.d(TAG, "VisualLearningHistoryActivity onCreate");

        try {
            initViews();
            setupToolbar();
            setupRecyclerView();
            loadData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Initialize data components
        repository = new VisualLearningRepository(getApplication());
        sessionManager = new SessionManager(this);

        // Get current user
        User currentUser = sessionManager.getUserDetails();
        if (currentUser != null) {
            currentUserId = currentUser.getID();
            Log.d(TAG, "Current user ID: " + currentUserId);
        } else {
            Log.e(TAG, "No current user found");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch sử từ vựng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.d(TAG, "Toolbar setup completed");
    }

    private void setupRecyclerView() {
        adapter = new VisualLearningAdapter(this, new VisualLearningAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VisualLearning item) {
                openDetailDialog(item);
            }

            @Override
            public void onFavoriteClick(VisualLearning item) {
                toggleFavorite(item);
            }

            @Override
            public void onDeleteClick(VisualLearning item) {
                deleteItem(item);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup swipe refresh
        swipeRefresh.setOnRefreshListener(this::refreshData);
        swipeRefresh.setColorSchemeResources(R.color.primary);

        Log.d(TAG, "RecyclerView setup completed");
    }

    private void loadData() {
        Log.d(TAG, "Loading visual learning history");
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        // Observe all visual learning data for current user
        repository.getAllByHocVien(currentUserId).observe(this, new Observer<List<VisualLearning>>() {
            @Override
            public void onChanged(List<VisualLearning> visualLearningList) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (visualLearningList != null && !visualLearningList.isEmpty()) {
                    Log.d(TAG, "Loaded " + visualLearningList.size() + " items");
                    adapter.updateData(visualLearningList);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "No items found");
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void refreshData() {
        Log.d(TAG, "Refreshing data");
        loadData();
    }

    private void searchData(String query) {
        Log.d(TAG, "Searching for: " + query);
        progressBar.setVisibility(View.VISIBLE);

        if (query.trim().isEmpty()) {
            loadData();
            return;
        }

        repository.search(query.trim()).observe(this, new Observer<List<VisualLearning>>() {
            @Override
            public void onChanged(List<VisualLearning> results) {
                progressBar.setVisibility(View.GONE);

                if (results != null && !results.isEmpty()) {
                    Log.d(TAG, "Search found " + results.size() + " items");
                    adapter.updateData(results);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "No search results");
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void openDetailDialog(VisualLearning item) {
        Log.d(TAG, "Opening detail for item: " + item.getId());

        // Create detail dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(item.getDetectedObject())
                .setMessage(
                        "🇨🇳 " + item.getChineseVocabulary() + "\n" +
                                "📝 " + item.getPinyin() + "\n" +
                                "🇻🇳 " + item.getVietnameseMeaning() + "\n\n" +
                                "📖 " + item.getExampleSentence() + "\n\n" +
                                "📅 " + formatDate(item.getCreatedAt())
                )
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Xem ảnh", (dialog, which) -> {
                    // TODO: Show image if exists
                    Toast.makeText(this, "Chức năng xem ảnh đang phát triển", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void toggleFavorite(VisualLearning item) {
        Log.d(TAG, "Toggling favorite for item: " + item.getId());

        boolean newFavoriteStatus = !item.isFavorite();
        repository.updateFavorite(item.getId(), newFavoriteStatus);

        String message = newFavoriteStatus ? "Đã thêm vào yêu thích" : "Đã bỏ khỏi yêu thích";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteItem(VisualLearning item) {
        Log.d(TAG, "Deleting item: " + item.getId());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa từ vựng")
                .setMessage("Bạn có chắc muốn xóa \"" + item.getDetectedObject() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.delete(item);
                    Toast.makeText(this, "Đã xóa từ vựng", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visual_learning_history, menu);

        // Setup search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Tìm kiếm từ vựng...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    loadData();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshData();
            return true;
        } else if (id == R.id.action_clear_all) {
            showClearAllDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showClearAllDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa tất cả")
                .setMessage("Bạn có chắc muốn xóa tất cả từ vựng đã lưu?")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> {
                    repository.deleteAll(currentUserId);
                    Toast.makeText(this, "Đã xóa tất cả từ vựng", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "VisualLearningHistoryActivity destroyed");
    }
}