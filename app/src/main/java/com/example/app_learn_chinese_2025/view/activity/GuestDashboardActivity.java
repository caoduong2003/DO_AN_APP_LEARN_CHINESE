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

import androidx.appcompat.app.AlertDialog;
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

    // 🔧 THÊM BIẾN ĐỂ QUẢN LÝ DIALOG VÀ LIFECYCLE
    private AlertDialog currentDialog;
    private boolean isActivityDestroyed = false;

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
        // 🔧 SAFE CHECK TRƯỚC KHI HIỂN THỊ DIALOG
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

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

        try {
            new AlertDialog.Builder(this)
                    .setTitle("Thông tin chế độ khách")
                    .setMessage(profileInfo)
                    .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                        safeNavigateToRegister();
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing profile dialog: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.guest_menu, menu);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating options menu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isActivityDestroyed || isFinishing()) {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        try {
            if (id == R.id.action_upgrade) {
                showUpgradeDialog();
                return true;
            } else if (id == R.id.action_login) {
                safeNavigateToLogin();
                return true;
            } else if (id == R.id.action_register) {
                safeNavigateToRegister();
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
        } catch (Exception e) {
            Log.e(TAG, "Error handling menu item: " + e.getMessage());
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        // 🔧 SAFE CHECK
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            new AlertDialog.Builder(this)
                    .setTitle("Trợ giúp")
                    .setIcon(R.drawable.ic_help)
                    .setMessage("Hướng dẫn sử dụng chế độ khách:\n\n" +
                            "• Bạn có thể xem 3 bài giảng mỗi ngày\n" +
                            "• Mỗi bài giảng hiển thị 5 từ vựng đầu tiên\n" +
                            "• Có thể dịch thuật 10 lần mỗi ngày\n" +
                            "• Đăng ký để trải nghiệm đầy đủ!")
                    .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                        safeNavigateToRegister();
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing help dialog: " + e.getMessage());
        }
    }

    private void showAboutDialog() {
        // 🔧 SAFE CHECK
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            new AlertDialog.Builder(this)
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing about dialog: " + e.getMessage());
        }
    }

    // GuestController.OnGuestDataListener Implementation
    @Override
    public void onStatsLoaded(Map<String, Object> stats) {
        Log.d(TAG, "📊 Stats loaded: " + stats.toString());
        runOnUiThread(() -> {
            if (!isActivityDestroyed && !isFinishing()) {
                showLoading(false);
                updateUsageInfo();
            }
        });
    }

    @Override
    public void onBaiGiangListLoaded(List<BaiGiang> baiGiangList) {
        Log.d(TAG, "📚 BaiGiang list loaded: " + baiGiangList.size() + " items");

        runOnUiThread(() -> {
            if (!isActivityDestroyed && !isFinishing()) {
                showLoading(false);
                this.baiGiangList.clear();
                this.baiGiangList.addAll(baiGiangList);
                baiGiangAdapter.notifyDataSetChanged();

                safeShowToast("Tải được " + baiGiangList.size() + " bài giảng");
            }
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

        if (!isActivityDestroyed && !isFinishing()) {
            runOnUiThread(() -> {
                showLoading(false);
                safeShowToast("Lỗi: " + message);
            });
        }
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
            try {
                Intent intent = new Intent(this, GuestLessonDetailActivity.class);
                intent.putExtra("baiGiangId", baiGiang.getID());
                intent.putExtra("tieuDe", baiGiang.getTieuDe());
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening lesson detail: " + e.getMessage());
                safeShowToast("Lỗi mở bài giảng");
            }
        }
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Not available in guest mode
        safeShowToast("Chức năng chỉ dành cho tài khoản đã đăng ký");
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Not available in guest mode
        safeShowToast("Chức năng chỉ dành cho tài khoản đã đăng ký");
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

        // Reset destroyed flag
        isActivityDestroyed = false;

        Log.d(TAG, "GuestDashboardActivity resumed");

        // Update usage info when returning to activity
        updateUsageInfo();

        // Check if user logged in
        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "User logged in, finishing guest dashboard");
            finish();
        }
    }

    // 🔧 FIX CHÍNH: onBackPressed() KHÔNG GỌI super.onBackPressed() TRƯỚC
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed in guest dashboard");

        // 🚨 KIỂM TRA ACTIVITY KHÔNG BỊ DESTROY
        if (isActivityDestroyed || isFinishing()) {
            Log.w(TAG, "Activity is finishing or destroyed, skipping dialog");
            super.onBackPressed();
            return;
        }

        // 🔧 DISMISS DIALOG CŨ NẾU TỒN TẠI
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null;
        }

        // 🎯 TẠO DIALOG MỚI VỚI SAFE CHECK
        try {
            currentDialog = new AlertDialog.Builder(this)
                    .setTitle("Thoát chế độ khách")
                    .setMessage("Bạn muốn:")
                    .setPositiveButton("Đăng nhập/Đăng ký", (dialog, which) -> {
                        // Quay về Welcome để đăng nhập
                        safeBackToWelcome();
                    })
                    .setNegativeButton("Thoát ứng dụng", (dialog, which) -> {
                        // Thoát hoàn toàn
                        safeFinishAffinity();
                    })
                    .setNeutralButton("Ở lại", (dialog, which) -> {
                        // Chỉ dismiss dialog
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(dialog -> {
                        // Clear reference khi dialog bị dismiss
                        currentDialog = null;
                    })
                    .create();

            // 🚨 HIỂN THỊ DIALOG CHỈ KHI ACTIVITY KHÔNG BỊ DESTROY
            if (!isActivityDestroyed && !isFinishing()) {
                currentDialog.show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog: " + e.getMessage());
            // Fallback: thoát trực tiếp
            super.onBackPressed();
        }
    }

    /**
     * 🔙 Safe method để quay về Welcome
     */
    private void safeBackToWelcome() {
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            Log.d(TAG, "Going back to Welcome screen");

            // Clear guest session
            if (sessionManager != null) {
                sessionManager.clearSession();
            }

            // Navigate to Welcome Activity
            Intent intent = new Intent(this, GuestWelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to welcome: " + e.getMessage());
            finish();
        }
    }

    /**
     * 🚪 Safe method để thoát app
     */
    private void safeFinishAffinity() {
        try {
            finishAffinity();
        } catch (Exception e) {
            Log.e(TAG, "Error finishing affinity: " + e.getMessage());
            finish();
        }
    }

    /**
     * 🎯 Safe method để chuyển đến Login
     */
    private void safeNavigateToLogin() {
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login: " + e.getMessage());
        }
    }

    /**
     * 🎯 Safe method để chuyển đến Register
     */
    private void safeNavigateToRegister() {
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to register: " + e.getMessage());
        }
    }

    /**
     * 🍞 Safe method để hiển thị Toast
     */
    private void safeShowToast(String message) {
        if (isActivityDestroyed || isFinishing()) {
            return;
        }

        try {
            runOnUiThread(() -> {
                if (!isActivityDestroyed && !isFinishing()) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 🔧 MARK ACTIVITY AS DESTROYED
        isActivityDestroyed = true;

        // 🔧 DISMISS DIALOG NẾU VẪN ĐANG HIỂN THỊ
        if (currentDialog != null && currentDialog.isShowing()) {
            try {
                currentDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing dialog in onDestroy: " + e.getMessage());
            } finally {
                currentDialog = null;
            }
        }

        Log.d(TAG, "GuestDashboardActivity destroyed safely");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 🔧 DISMISS DIALOG KHI ACTIVITY PAUSE
        if (currentDialog != null && currentDialog.isShowing()) {
            try {
                currentDialog.dismiss();
                currentDialog = null;
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing dialog in onPause: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 🔧 EXTRA SAFETY: DISMISS DIALOG KHI ACTIVITY STOP
        if (currentDialog != null && currentDialog.isShowing()) {
            try {
                currentDialog.dismiss();
                currentDialog = null;
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing dialog in onStop: " + e.getMessage());
            }
        }
    }
}