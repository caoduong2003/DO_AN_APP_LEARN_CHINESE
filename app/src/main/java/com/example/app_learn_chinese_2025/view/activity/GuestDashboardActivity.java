package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.GuestController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.util.GuestLimitationHelper;
import com.example.app_learn_chinese_2025.util.GuestUsageTracker;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 🚀 Dashboard chính cho Guest Mode
 */
public class GuestDashboardActivity extends AppCompatActivity implements
        GuestController.OnGuestDataListener, BaiGiangAdapter.OnBaiGiangItemClickListener {

    private static final String TAG = "GuestDashboardActivity";

    // UI Components
    private Toolbar toolbar;
    private TextView tvWelcome, tvUsageInfo, tvLimitationInfo;
    private Button btnUpgradeAccount, btnViewProfile;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewBaiGiang;
    private ProgressBar progressBar;

    // Data & Controllers
    private GuestController guestController;
    private GuestLimitationHelper limitationHelper;
    private SessionManager sessionManager;
    private BaiGiangAdapter baiGiangAdapter;
    private List<BaiGiang> baiGiangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        Log.d(TAG, "🚀 GuestDashboardActivity created");

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        setupControllers();

        loadGuestData();
        setupUpgradePrompts();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUsageInfo = findViewById(R.id.tvUsageInfo);
        tvLimitationInfo = findViewById(R.id.tvLimitationInfo);
        btnUpgradeAccount = findViewById(R.id.btnUpgradeAccount);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewBaiGiang = findViewById(R.id.recyclerViewBaiGiang);
        progressBar = findViewById(R.id.progressBar);

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chế độ khách");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        Log.d(TAG, "✅ Toolbar setup complete");
    }

    private void setupRecyclerView() {
        baiGiangList = new ArrayList<>();
        baiGiangAdapter = new BaiGiangAdapter(this, baiGiangList, this);

        recyclerViewBaiGiang.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBaiGiang.setAdapter(baiGiangAdapter);

        Log.d(TAG, "✅ RecyclerView setup complete");
    }

    private void setupListeners() {
        btnUpgradeAccount.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User clicked upgrade account");
            showUpgradeDialog();
        });

        btnViewProfile.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User clicked view profile");
            showGuestProfileDialog();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "🔄 User triggered refresh");
            loadGuestData();
        });

        Log.d(TAG, "✅ Listeners setup complete");
    }

    private void setupControllers() {
        sessionManager = new SessionManager(this);
        guestController = new GuestController(this, this);
        limitationHelper = new GuestLimitationHelper(this);

        Log.d(TAG, "✅ Controllers setup complete");
    }

    private void loadGuestData() {
        Log.d(TAG, "📊 Loading guest data");

        showLoading(true);

        // Load stats
        guestController.getGuestStats();

        // Load bai giang list
        guestController.getGuestBaiGiang(10, null, null);

        // Update usage info
        updateUsageInfo();
    }

    private void setupUpgradePrompts() {
        Log.d(TAG, "🎯 Setting up upgrade prompts");

        // Welcome tips cho lần đầu sử dụng
        limitationHelper.showWelcomeTips(this);

        // Smart upgrade prompt
        limitationHelper.showSmartUpgradePrompt(this);

        Log.d(TAG, "✅ Upgrade prompts setup complete");
    }

    private void updateUsageInfo() {
        GuestUsageTracker.UsageStats stats = limitationHelper.getUsageTracker().getTodayStats();
        GuestUsageTracker.RemainingUsage remaining = limitationHelper.getUsageTracker().getRemainingUsage();

        tvWelcome.setText("Chào mừng, " + sessionManager.getDisplayName() + "!");

        String usageText = String.format("Hôm nay: %d/%d bài giảng • %d/%d lần dịch",
                stats.lessonsToday, stats.maxLessonsPerDay,
                stats.translationsToday, stats.maxTranslationsPerDay);
        tvUsageInfo.setText(usageText);

        String limitText = String.format("Còn lại: %d bài giảng • %d lần dịch",
                remaining.remainingLessons, remaining.remainingTranslations);
        tvLimitationInfo.setText(limitText);

        Log.d(TAG, "📊 Usage info updated: " + usageText);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showUpgradeDialog() {
        limitationHelper.showSmartUpgradePrompt(this);
    }

    private void showGuestProfileDialog() {
        GuestUsageTracker.UsageStats stats = limitationHelper.getUsageTracker().getTodayStats();

        String profileInfo = String.format(
                "Thông tin khách:\n\n" +
                        "• Device ID: %s\n" +
                        "• Tổng sessions: %d\n" +
                        "• Ngày đầu sử dụng: %s\n" +
                        "• Bài giảng hôm nay: %d/%d\n" +
                        "• Dịch thuật hôm nay: %d/%d",
                sessionManager.getGuestDeviceId(),
                stats.totalSessions,
                stats.firstUsageDate,
                stats.lessonsToday, stats.maxLessonsPerDay,
                stats.translationsToday, stats.maxTranslationsPerDay
        );

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thông tin chế độ khách")
                .setMessage(profileInfo)
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_upgrade) {
            showUpgradeDialog();
            return true;
        } else if (id == R.id.action_login) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (id == R.id.action_register) {
            startActivity(new Intent(this, RegisterActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            loadGuestData();
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Trợ giúp")
                .setIcon(R.drawable.ic_help)
                .setMessage("Hướng dẫn sử dụng chế độ khách:\n\n" +
                        "• Bạn có thể xem 3 bài giảng mỗi ngày\n" +
                        "• Mỗi bài giảng hiển thị 5 từ vựng đầu tiên\n" +
                        "• Có thể dịch thuật 10 lần mỗi ngày\n" +
                        "• Đăng ký để trải nghiệm đầy đủ!")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Giới thiệu")
                .setIcon(R.drawable.ic_info)
                .setMessage("Ứng dụng Học Tiếng Trung\n\n" +
                        "Phiên bản: 1.0.0\n" +
                        "Phát triển bởi: Nhóm phát triển\n\n" +
                        "Ứng dụng giúp bạn học tiếng Trung một cách hiệu quả với:\n" +
                        "• Bài giảng phong phú\n" +
                        "• Từ vựng chi tiết\n" +
                        "• Dịch thuật thông minh\n" +
                        "• Theo dõi tiến trình")
                .setPositiveButton("Đóng", null)
                .show();
    }

    // GuestController.OnGuestDataListener Implementation
    @Override
    public void onStatsLoaded(Map<String, Object> stats) {
        Log.d(TAG, "📊 Stats loaded: " + stats.toString());
        runOnUiThread(() -> {
            showLoading(false);
            updateUsageInfo();
        });
    }

    @Override
    public void onBaiGiangListLoaded(List<BaiGiang> baiGiangList) {
        Log.d(TAG, "📚 BaiGiang list loaded: " + baiGiangList.size() + " items");

        runOnUiThread(() -> {
            showLoading(false);
            this.baiGiangList.clear();
            this.baiGiangList.addAll(baiGiangList);
            baiGiangAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Tải được " + baiGiangList.size() + " bài giảng",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBaiGiangDetailLoaded(BaiGiang baiGiang) {
        Log.d(TAG, "📖 BaiGiang detail loaded: " + baiGiang.getTieuDe());
        // Handle detail if needed
    }

    @Override
    public void onTuVungListLoaded(List<TuVung> tuVungList) {
        Log.d(TAG, "📝 TuVung list loaded: " + tuVungList.size() + " items");
        // Handle vocabulary if needed
    }

    @Override
    public void onChuDeListLoaded(List<ChuDe> chuDeList) {
        Log.d(TAG, "🏷️ ChuDe list loaded: " + chuDeList.size() + " items");
        // Handle topics if needed
    }

    @Override
    public void onCapDoHSKListLoaded(List<CapDoHSK> capDoHSKList) {
        Log.d(TAG, "📊 CapDoHSK list loaded: " + capDoHSKList.size() + " items");
        // Handle HSK levels if needed
    }

    @Override
    public void onLoaiBaiGiangListLoaded(List<LoaiBaiGiang> loaiBaiGiangList) {
        Log.d(TAG, "🏷️ LoaiBaiGiang list loaded: " + loaiBaiGiangList.size() + " items");
        // Handle lesson types if needed
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "❌ Error: " + message);

        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    // BaiGiangAdapter.OnBaiGiangItemClickListener Implementation
    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d(TAG, "🎯 User clicked bai giang: " + baiGiang.getTieuDe());

        // Check limitation before accessing
        if (limitationHelper.checkAndShowLessonLimitation(this)) {
            // Record access
            limitationHelper.recordLessonAccess();

            // Update usage display
            updateUsageInfo();

            // Open lesson detail
            Intent intent = new Intent(this, GuestLessonDetailActivity.class);
            intent.putExtra("baiGiangId", baiGiang.getID());
            intent.putExtra("tieuDe", baiGiang.getTieuDe());
            startActivity(intent);
        }
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Not available in guest mode
        Toast.makeText(this, "Chức năng chỉ dành cho tài khoản đã đăng ký",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Not available in guest mode
        Toast.makeText(this, "Chức năng chỉ dành cho tài khoản đã đăng ký",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayVideo(BaiGiang baiGiang) {
        Log.d(TAG, "🎥 Playing video for: " + baiGiang.getTieuDe());
        // Handle video playback
    }

    @Override
    public void onPlayAudio(BaiGiang baiGiang) {
        Log.d(TAG, "🔊 Playing audio for: " + baiGiang.getTieuDe());
        // Handle audio playback
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GuestDashboardActivity resumed");

        // Update usage info when returning to activity
        updateUsageInfo();

        // Check if user logged in
        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "User logged in, finishing guest dashboard");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed in guest dashboard");

        // Show exit confirmation
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có muốn thoát khỏi ứng dụng?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}