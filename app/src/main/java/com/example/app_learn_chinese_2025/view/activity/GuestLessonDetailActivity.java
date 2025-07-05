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

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.GuestController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.util.GuestLimitationHelper;
import com.example.app_learn_chinese_2025.view.adapter.GuestTuVungAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 🚀 Chi tiết bài giảng cho Guest Mode
 */
public class GuestLessonDetailActivity extends AppCompatActivity implements
        GuestController.OnGuestDataListener, GuestTuVungAdapter.OnTuVungClickListener {
    private static final String TAG = "GuestLessonDetailActivity";

    // UI Components
    private Toolbar toolbar;
    private TextView tvTitle, tvDescription, tvLimitInfo, tvVocabularyTitle;
    private Button btnUpgrade, btnViewMore;
    private RecyclerView recyclerViewTuVung;
    private ProgressBar progressBar;

    // Data & Controllers
    private GuestController guestController;
    private GuestLimitationHelper limitationHelper;
    private GuestTuVungAdapter tuVungAdapter;
    private List<TuVung> tuVungList;

    private long baiGiangId;
    private String tieuDe;
    private BaiGiang currentBaiGiang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_lesson_detail);

        Log.d(TAG, "🚀 GuestLessonDetailActivity created");

        getIntentData();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        setupControllers();

        loadLessonData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        baiGiangId = intent.getLongExtra("baiGiangId", -1);
        tieuDe = intent.getStringExtra("tieuDe");

        Log.d(TAG, "📖 Loading lesson: " + baiGiangId + " - " + tieuDe);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvLimitInfo = findViewById(R.id.tvLimitInfo);
        tvVocabularyTitle = findViewById(R.id.tvVocabularyTitle);
        btnUpgrade = findViewById(R.id.btnUpgrade);
        btnViewMore = findViewById(R.id.btnViewMore);
        recyclerViewTuVung = findViewById(R.id.recyclerViewTuVung);
        progressBar = findViewById(R.id.progressBar);

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(tieuDe != null ? tieuDe : "Chi tiết bài giảng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Log.d(TAG, "✅ Toolbar setup complete");
    }

    private void setupRecyclerView() {
        tuVungList = new ArrayList<>();
        tuVungAdapter = new GuestTuVungAdapter(this, tuVungList, this);

        recyclerViewTuVung.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTuVung.setAdapter(tuVungAdapter);

        Log.d(TAG, "✅ RecyclerView setup complete");
    }

    private void setupListeners() {
        btnUpgrade.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User clicked upgrade from lesson detail");
            showUpgradeDialog();
        });

        btnViewMore.setOnClickListener(v -> {
            Log.d(TAG, "🎯 User clicked view more vocabulary");
            showViewMoreDialog();
        });

        Log.d(TAG, "✅ Listeners setup complete");
    }

    private void setupControllers() {
        guestController = new GuestController(this, this);
        limitationHelper = new GuestLimitationHelper(this);

        Log.d(TAG, "✅ Controllers setup complete");
    }

    private void loadLessonData() {
        Log.d(TAG, "📊 Loading lesson data for: " + baiGiangId);

        showLoading(true);

        // Load lesson detail
        guestController.getGuestBaiGiangDetail(baiGiangId);

        // Load vocabulary với limitation check
        if (limitationHelper.checkAndShowVocabularyLimitation(this, baiGiangId)) {
            guestController.getGuestTuVung(baiGiangId, 5); // Guest limit: 5 từ vựng
        }

        updateLimitInfo();
    }

    private void updateLimitInfo() {
        String limitText = "Chế độ khách: Hiển thị 5 từ vựng đầu tiên";
        tvLimitInfo.setText(limitText);
        tvLimitInfo.setVisibility(View.VISIBLE);

        tvVocabularyTitle.setText("Từ vựng (5 từ đầu tiên)");

        Log.d(TAG, "📊 Limit info updated");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showUpgradeDialog() {
        limitationHelper.showSmartUpgradePrompt(this);
    }

    private void showViewMoreDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xem thêm từ vựng")
                .setIcon(R.drawable.ic_vocabulary)
                .setMessage("Bạn chỉ có thể xem 5 từ vựng đầu tiên trong chế độ khách.\n\n" +
                        "Đăng ký để xem tất cả từ vựng và nhiều tính năng khác:")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_lesson_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_upgrade) {
            showUpgradeDialog();
            return true;
        } else if (id == R.id.action_share) {
            shareLesson();
            return true;
        } else if (id == R.id.action_refresh) {
            loadLessonData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareLesson() {
        String shareText = "Tôi đang học bài: " + tieuDe +
                "\n\nTải app Học Tiếng Trung để học cùng nhau!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài học"));
    }

    // GuestController.OnGuestDataListener Implementation
    @Override
    public void onStatsLoaded(Map<String, Object> stats) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangListLoaded(List<BaiGiang> baiGiangList) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangDetailLoaded(BaiGiang baiGiang) {
        Log.d(TAG, "📖 BaiGiang detail loaded: " + baiGiang.getTieuDe());

        runOnUiThread(() -> {
            currentBaiGiang = baiGiang;

            tvTitle.setText(baiGiang.getTieuDe());
            tvDescription.setText(baiGiang.getMoTa() != null ? baiGiang.getMoTa() : "Không có mô tả");

            // Update toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(baiGiang.getTieuDe());
            }

            showLoading(false);
        });
    }

    @Override
    public void onTuVungListLoaded(List<TuVung> tuVungList) {
        Log.d(TAG, "📝 TuVung list loaded: " + tuVungList.size() + " items");

        runOnUiThread(() -> {
            this.tuVungList.clear();
            this.tuVungList.addAll(tuVungList);
            tuVungAdapter.notifyDataSetChanged();

            showLoading(false);

            // Show "View More" button if there might be more vocabulary
            btnViewMore.setVisibility(tuVungList.size() >= 5 ? View.VISIBLE : View.GONE);

            Toast.makeText(this, "Tải được " + tuVungList.size() + " từ vựng",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onChuDeListLoaded(List<ChuDe> chuDeList) {
        // Not used in this activity
    }

    @Override
    public void onCapDoHSKListLoaded(List<CapDoHSK> capDoHSKList) {
        // Not used in this activity
    }

    @Override
    public void onLoaiBaiGiangListLoaded(List<LoaiBaiGiang> loaiBaiGiangList) {
        // Not used in this activity
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "❌ Error: " + message);

        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GuestLessonDetailActivity resumed");

        // Update limit info when returning to activity
        updateLimitInfo();
    }

    /**
     * 🎯 onClick method cho login button trong layout
     */
    public void startLoginActivity(View view) {
        Log.d(TAG, "🎯 User clicked login from lesson detail");
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 🎯 onClick method cho register button trong layout
     */
    public void startRegisterActivity(View view) {
        Log.d(TAG, "🎯 User clicked register from lesson detail");
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // GuestTuVungAdapter.OnTuVungClickListener Implementation
    @Override
    public void onPlayAudio(TuVung tuVung) {
        Log.d(TAG, "🔊 Play audio for: " + tuVung.getTiengTrungDisplay());

        if (tuVung.hasAudio()) {
            Toast.makeText(this, "Đang phát âm thanh: " + tuVung.getTiengTrungDisplay(), Toast.LENGTH_SHORT).show();
            // TODO: Implement actual audio playback with tuVung.getAudioURL()
        } else {
            Toast.makeText(this, "Phát âm: " + tuVung.getTiengTrungDisplay() + " (" + tuVung.getPhienAmDisplay() + ")", Toast.LENGTH_SHORT).show();
            // TODO: Implement text-to-speech
        }
    }

    @Override
    public void onTranslate(TuVung tuVung) {
        Log.d(TAG, "🔤 Translate: " + tuVung.getTiengTrungDisplay());

        // Check translation limitation
        if (limitationHelper.checkAndShowTranslationLimitation(this)) {
            // Record translation usage
            limitationHelper.recordTranslationUsage();

            // Show translation using full translation method
            String translationText = tuVung.getFullTranslation();

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Dịch thuật")
                    .setIcon(R.drawable.ic_translate)
                    .setMessage(translationText)
                    .setPositiveButton("Đóng", null)
                    .show();
        }
    }

    @Override
    public void onViewMore(TuVung tuVung) {
        Log.d(TAG, "👁️ View more for: " + tuVung.getTiengTrungDisplay());

        // Show detailed info dialog
        String detailText = tuVung.getFullTranslation();

        if (tuVung.hasExample()) {
            detailText += "\n\n📝 Ví dụ: " + tuVung.getViDu();
        }

        if (tuVung.hasImage()) {
            detailText += "\n\n🖼️ Có hình ảnh minh họa";
        }

        if (tuVung.hasAudio()) {
            detailText += "\n\n🔊 Có file âm thanh";
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chi tiết từ vựng")
                .setIcon(R.drawable.ic_vocabulary)
                .setMessage(detailText)
                .setPositiveButton("Đóng", null)
                .setNegativeButton("Đăng ký để xem thêm", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .show();
    }
}