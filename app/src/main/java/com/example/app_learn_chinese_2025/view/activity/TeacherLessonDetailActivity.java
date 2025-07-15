package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.TeacherBaiGiangController;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 🎯 Activity hiển thị chi tiết bài giảng cho giáo viên
 * Hỗ trợ xem, chỉnh sửa, xóa bài giảng
 */
public class TeacherLessonDetailActivity extends AppCompatActivity
        implements TeacherBaiGiangController.OnTeacherBaiGiangListener {

    private static final String TAG = "TeacherLessonDetail";
    private static final int REQUEST_EDIT_LESSON = 1001;

    // UI Components
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton fabEdit;

    // Header
    private ImageView ivHeaderImage;
    private TextView tvStatusBadge, tvPremiumBadge;

    // Content
    private TextView tvTitle, tvDescription, tvMetaInfo, tvDuration;
    private TextView tvViewCount, tvCreatedDate, tvUpdatedDate;
    private TextView tvContent, tvVideoUrl, tvAudioUrl;

    // Media Controls
    private LinearLayout layoutVideoUrl, layoutAudioUrl, cardMedia;
    private ImageButton btnPlayVideo, btnPlayAudio;

    // Empty State
    private LinearLayout layoutEmptyState;

    // Data & Controllers
    private SessionManager sessionManager;
    private TeacherBaiGiangController controller;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Current Data
    private Long lessonId;
    private ApiService.TeacherBaiGiangResponse.DetailResponse currentLesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_lesson_detail);

        Log.d(TAG, "🚀 onCreate started");

        getIntentData();
        initViews();
        setupToolbar();
        setupListeners();
        loadLessonDetail();
    }

    // ===== INITIALIZATION METHODS =====

    private void getIntentData() {
        if (getIntent().hasExtra("LESSON_ID")) {
            lessonId = getIntent().getLongExtra("LESSON_ID", -1);
            if (lessonId == -1) {
                Log.e(TAG, "❌ Invalid lesson ID");
                Toast.makeText(this, "ID bài giảng không hợp lệ", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Log.e(TAG, "❌ No lesson ID provided");
            Toast.makeText(this, "Không tìm thấy bài giảng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "📋 Lesson ID: " + lessonId);
    }

    private void initViews() {
        // Toolbar & Layout
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        progressBar = findViewById(R.id.progressBar);
        fabEdit = findViewById(R.id.fabEdit);

        // Header
        ivHeaderImage = findViewById(R.id.ivHeaderImage);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvPremiumBadge = findViewById(R.id.tvPremiumBadge);

        // Content
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvMetaInfo = findViewById(R.id.tvMetaInfo);
        tvDuration = findViewById(R.id.tvDuration);

        // Statistics
        tvViewCount = findViewById(R.id.tvViewCount);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        tvUpdatedDate = findViewById(R.id.tvUpdatedDate);

        // Content & Media
        tvContent = findViewById(R.id.tvContent);
        tvVideoUrl = findViewById(R.id.tvVideoUrl);
        tvAudioUrl = findViewById(R.id.tvAudioUrl);

        // Media Controls
        layoutVideoUrl = findViewById(R.id.layoutVideoUrl);
        layoutAudioUrl = findViewById(R.id.layoutAudioUrl);
        cardMedia = findViewById(R.id.cardMedia);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);

        // Empty State
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        // Initialize controllers
        sessionManager = new SessionManager(this);
        controller = new TeacherBaiGiangController(this, this);

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        collapsingToolbar.setTitle("Chi tiết bài giảng");
    }

    private void setupListeners() {
        // Edit FAB
        fabEdit.setOnClickListener(v -> editLesson());

        // Media controls
        btnPlayVideo.setOnClickListener(v -> playVideo());
        btnPlayAudio.setOnClickListener(v -> playAudio());

        Log.d(TAG, "✅ Listeners setup completed");
    }

    // ===== DATA LOADING =====

    private void loadLessonDetail() {
        Log.d(TAG, "🌐 Loading lesson detail for ID: " + lessonId);
        showProgress(true);
        showEmptyState(false);
        controller.getBaiGiangDetail(lessonId);
    }

    private void populateData(ApiService.TeacherBaiGiangResponse.DetailResponse lesson) {
        Log.d(TAG, "📝 Populating data for lesson: " + lesson.getTieuDe());

        currentLesson = lesson;

        // Update toolbar title
        collapsingToolbar.setTitle(lesson.getTieuDe());

        // Basic info
        tvTitle.setText(lesson.getTieuDe());
        tvDescription.setText(lesson.getMoTa() != null ? lesson.getMoTa() : "Không có mô tả");

        // Meta information
        StringBuilder metaInfo = new StringBuilder();
        if (lesson.getCapDoHSK() != null) {
            metaInfo.append("HSK ").append(lesson.getCapDoHSK().getCapDo());
        }
        if (lesson.getChuDe() != null) {
            if (metaInfo.length() > 0) metaInfo.append(" • ");
            metaInfo.append(lesson.getChuDe().getTen());
        }
        if (lesson.getLoaiBaiGiang() != null) {
            if (metaInfo.length() > 0) metaInfo.append(" • ");
            metaInfo.append(lesson.getLoaiBaiGiang().getTen());
        }
        tvMetaInfo.setText(metaInfo.toString());

        // Duration
        if (lesson.getThoiLuong() != null) {
            tvDuration.setText("⏱ " + lesson.getThoiLuong() + " phút");
        } else {
            tvDuration.setText("⏱ Chưa xác định");
        }

        // Statistics
        tvViewCount.setText(String.valueOf(lesson.getLuotXem() != null ? lesson.getLuotXem() : 0));

        // Dates
        if (lesson.getNgayTao() != null) {
            try {
                Date date = inputFormat.parse(lesson.getNgayTao());
                tvCreatedDate.setText(outputFormat.format(date));
            } catch (ParseException e) {
                tvCreatedDate.setText("N/A");
            }
        } else {
            tvCreatedDate.setText("N/A");
        }

        if (lesson.getNgayCapNhat() != null) {
            try {
                Date date = inputFormat.parse(lesson.getNgayCapNhat());
                tvUpdatedDate.setText(outputFormat.format(date));
            } catch (ParseException e) {
                tvUpdatedDate.setText("N/A");
            }
        } else {
            tvUpdatedDate.setText("N/A");
        }

        // Content
        tvContent.setText(lesson.getNoiDung() != null ? lesson.getNoiDung() : "Không có nội dung");

        // Status badges
        updateStatusBadges(lesson);

        // Header image
        loadHeaderImage(lesson.getHinhAnh());

        // Media URLs
        setupMediaUrls(lesson);

        Log.d(TAG, "✅ Data populated successfully");
    }

    private void updateStatusBadges(ApiService.TeacherBaiGiangResponse.DetailResponse lesson) {
        // Status badge
        if (lesson.getTrangThai() != null && lesson.getTrangThai()) {
            tvStatusBadge.setText("Công khai");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_success);
            tvStatusBadge.setVisibility(View.VISIBLE);
        } else {
            tvStatusBadge.setText("Ẩn");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            tvStatusBadge.setVisibility(View.VISIBLE);
        }

        // Premium badge
        if (lesson.getLaBaiGiangGoi() != null && lesson.getLaBaiGiangGoi()) {
            tvPremiumBadge.setVisibility(View.VISIBLE);
        } else {
            tvPremiumBadge.setVisibility(View.GONE);
        }
    }

    private void loadHeaderImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            String imageUrl = Constants.getBaseUrl() + Constants.API_VIEW_IMAGE + imagePath;
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_lesson)
                    .error(R.drawable.placeholder_lesson)
                    .into(ivHeaderImage);
        } else {
            ivHeaderImage.setImageResource(R.drawable.placeholder_lesson);
        }
    }

    private void setupMediaUrls(ApiService.TeacherBaiGiangResponse.DetailResponse lesson) {
        boolean hasMedia = false;

        // Video URL
        if (lesson.getVideoURL() != null && !lesson.getVideoURL().isEmpty()) {
            tvVideoUrl.setText(lesson.getVideoURL());
            layoutVideoUrl.setVisibility(View.VISIBLE);
            hasMedia = true;
        } else {
            layoutVideoUrl.setVisibility(View.GONE);
        }

        // Audio URL
        if (lesson.getAudioURL() != null && !lesson.getAudioURL().isEmpty()) {
            tvAudioUrl.setText(lesson.getAudioURL());
            layoutAudioUrl.setVisibility(View.VISIBLE);
            hasMedia = true;
        } else {
            layoutAudioUrl.setVisibility(View.GONE);
        }

        // Show/hide media card
        cardMedia.setVisibility(hasMedia ? View.VISIBLE : View.GONE);
    }

    // ===== UI HELPER METHODS =====

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState(boolean show) {
        layoutEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        fabEdit.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // ===== ACTION METHODS =====

    private void editLesson() {
        if (currentLesson != null) {
            Intent intent = new Intent(this, TeacherCreateEditLessonActivity.class);
            intent.putExtra("LESSON_ID", currentLesson.getId());
            intent.putExtra("IS_EDIT_MODE", true);
            startActivityForResult(intent, REQUEST_EDIT_LESSON);
        }
    }

    private void deleteLesson() {
        if (currentLesson != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa bài giảng")
                    .setMessage("Bạn có chắc chắn muốn xóa bài giảng \"" + currentLesson.getTieuDe() + "\"?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        showProgress(true);
                        controller.deleteBaiGiang(currentLesson.getId());
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(R.drawable.ic_warning)
                    .show();
        }
    }

    private void shareLesson() {
        if (currentLesson != null) {
            String shareText = "Bài giảng: " + currentLesson.getTieuDe() + "\n" +
                    "Mô tả: " + (currentLesson.getMoTa() != null ? currentLesson.getMoTa() : "");

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài giảng"));
        }
    }

    private void playVideo() {
        if (currentLesson != null && currentLesson.getVideoURL() != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentLesson.getVideoURL()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playAudio() {
        if (currentLesson != null && currentLesson.getAudioURL() != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentLesson.getAudioURL()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ===== MENU HANDLING =====

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_lesson_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_edit) {
            editLesson();
            return true;
        } else if (itemId == R.id.action_delete) {
            deleteLesson();
            return true;
        } else if (itemId == R.id.action_share) {
            shareLesson();
            return true;
        } else if (itemId == R.id.action_refresh) {
            loadLessonDetail();
            return true;
        } else if (itemId == R.id.action_toggle_status) {
            toggleLessonStatus();
            return true;
        } else if (itemId == R.id.action_toggle_premium) {
            toggleLessonPremium();
            return true;
        } else if (itemId == R.id.action_duplicate) {
            duplicateLesson();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // ===== ADDITIONAL ACTION METHODS =====

    private void toggleLessonStatus() {
        if (currentLesson != null) {
            String action = currentLesson.getTrangThai() ? "ẩn" : "công khai";
            new AlertDialog.Builder(this)
                    .setTitle("Thay đổi trạng thái")
                    .setMessage("Bạn có muốn " + action + " bài giảng \"" + currentLesson.getTieuDe() + "\"?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        showProgress(true);
                        controller.toggleStatus(currentLesson.getId());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    private void toggleLessonPremium() {
        if (currentLesson != null) {
            String action = currentLesson.getLaBaiGiangGoi() ? "miễn phí" : "premium";
            new AlertDialog.Builder(this)
                    .setTitle("Thay đổi loại bài giảng")
                    .setMessage("Bạn có muốn chuyển bài giảng \"" + currentLesson.getTieuDe() + "\" thành " + action + "?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        showProgress(true);
                        controller.togglePremium(currentLesson.getId());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    private void duplicateLesson() {
        if (currentLesson != null) {
            android.widget.EditText editText = new android.widget.EditText(this);
            editText.setHint("Tiêu đề mới (tùy chọn)");

            new AlertDialog.Builder(this)
                    .setTitle("Nhân bản bài giảng")
                    .setMessage("Nhân bản bài giảng \"" + currentLesson.getTieuDe() + "\"")
                    .setView(editText)
                    .setPositiveButton("Nhân bản", (dialog, which) -> {
                        String newTitle = editText.getText().toString().trim();
                        showProgress(true);
                        controller.duplicateBaiGiang(currentLesson.getId(), newTitle.isEmpty() ? null : newTitle);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    // ===== ACTIVITY RESULT =====

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_LESSON && resultCode == RESULT_OK) {
            // Reload lesson data after edit
            loadLessonDetail();
        }
    }

    // ===== IMPLEMENT TeacherBaiGiangController.OnTeacherBaiGiangListener =====

    @Override
    public void onBaiGiangListReceived(ApiService.TeacherBaiGiangResponse.PageResponse response) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangDetailReceived(ApiService.TeacherBaiGiangResponse.DetailResponse baiGiang) {
        Log.d(TAG, "✅ Lesson detail received: " + baiGiang.getTieuDe());
        showProgress(false);
        populateData(baiGiang);
    }

    @Override
    public void onBaiGiangCreated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangUpdated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangDeleted() {
        Log.d(TAG, "✅ Lesson deleted successfully");
        showProgress(false);
        Toast.makeText(this, "Xóa bài giảng thành công", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onStatusToggled(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        // Not used in this activity
    }

    @Override
    public void onBaiGiangDuplicated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        // Not used in this activity
    }

    @Override
    public void onStatisticsReceived(ApiService.TeacherBaiGiangResponse.StatsResponse stats) {
        // Not used in this activity
    }

    @Override
    public void onSearchResultReceived(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> results) {
        // Not used in this activity
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "❌ Error: " + message);
        showProgress(false);

        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
        showEmptyState(true);
    }
}