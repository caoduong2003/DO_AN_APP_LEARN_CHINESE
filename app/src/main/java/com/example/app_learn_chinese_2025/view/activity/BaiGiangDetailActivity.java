package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;
import java.util.List;

public class BaiGiangDetailActivity extends AppCompatActivity implements BaiGiangController.OnBaiGiangListener {

    private static final String TAG = "BaiGiangDetailActivity";

    // UI Components
    private TextView tvTitle, tvDescription, tvNoVideo;
    private PlayerView playerView;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvLessonContent;
    private TextView tvHSKLevel, tvLessonType, tvDuration, tvViews;
    // Data
    private BaiGiang currentBaiGiang;
    private User currentUser;

    // Controllers
    private BaiGiangController baiGiangController;
    private SessionManager sessionManager;

    // Media
    private ExoPlayer player;

    // Timing
    private long startTime;
    private long totalWatchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai_giang_detail);

        try {
            initViews();
            initControllers();
            loadBaiGiang();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            showError("Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvNoVideo = findViewById(R.id.tvNoVideo);
        playerView = findViewById(R.id.playerView);
        progressBar = findViewById(R.id.progressBar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tvHSKLevel = findViewById(R.id.tvHSKLevel);
        tvLessonType = findViewById(R.id.tvLessonType);
        tvDuration = findViewById(R.id.tvDuration);
        tvViews = findViewById(R.id.tvViews);
        tvLessonContent = findViewById(R.id.tvLessonContent);

        if (tvNoVideo != null) {
            tvNoVideo.setVisibility(View.GONE);
        }
    }

    private void initControllers() {
        sessionManager = new SessionManager(this);
        baiGiangController = new BaiGiangController(this, this);
        currentUser = sessionManager.getUserDetails();
    }

    private void loadBaiGiang() {
        if (getIntent() == null) {
            showError("Không nhận được thông tin bài giảng");
            return;
        }

        long baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
        if (baiGiangId == -1) {
            showError("ID bài giảng không hợp lệ");
            return;
        }

        if (!isNetworkAvailable()) {
            showError("Không có kết nối mạng");
            return;
        }

        showLoading(true);
        baiGiangController.getBaiGiangById(baiGiangId);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network network = cm.getActiveNetwork();
        return network != null;
    }

    private void displayBaiGiang(BaiGiang baiGiang) {
        if (baiGiang == null) {
            showError("Dữ liệu bài giảng không hợp lệ");
            return;
        }

        try {
            // ✅ DEBUG: Log content for verification
            debugBaiGiangContent(baiGiang);

            // Display basic info
            tvTitle.setText(baiGiang.getTieuDe() != null ? baiGiang.getTieuDe() : "Không có tiêu đề");
            tvDescription.setText(baiGiang.getMoTa() != null ? baiGiang.getMoTa() : "Không có mô tả");

            // Display lesson metadata
            if (baiGiang.getCapDoHSK() != null) {
                tvHSKLevel.setText(baiGiang.getCapDoHSK().getTenCapDo());
                tvHSKLevel.setVisibility(View.VISIBLE);
            } else {
                tvHSKLevel.setVisibility(View.GONE);
            }

            if (baiGiang.getLoaiBaiGiang() != null) {
                tvLessonType.setText(baiGiang.getLoaiBaiGiang().getTenLoai());
                tvLessonType.setVisibility(View.VISIBLE);
            } else {
                tvLessonType.setVisibility(View.GONE);
            }

            tvDuration.setText(baiGiang.getThoiLuong() + " phút");
            tvViews.setText(baiGiang.getLuotXem() + " lượt xem");

            // ✅ MAIN FIX: Display detailed content (NoiDung)
            displayLessonContent(baiGiang.getNoiDung());

            // Setup video
            setupVideo(baiGiang.getVideoURL());
            startTime = System.currentTimeMillis();

        } catch (Exception e) {
            Log.e(TAG, "Error displaying BaiGiang: " + e.getMessage(), e);
            showError("Lỗi hiển thị bài giảng: " + e.getMessage());
        }
    }

    // ✅ NEW: Method to display and format lesson content
    private void displayLessonContent(String noiDung) {
        if (noiDung == null || noiDung.trim().isEmpty()) {
            tvLessonContent.setText("Nội dung bài giảng đang được cập nhật...");
            return;
        }

        // ✅ Format content for better display
        String formattedContent = formatLessonContent(noiDung);
        tvLessonContent.setText(formattedContent);
        Log.d(TAG, "✅ Displayed lesson content length: " + formattedContent.length() + " characters");
    }

    // ✅ NEW: Helper method to format lesson content
    private String formatLessonContent(String rawContent) {
        if (rawContent == null) return "";

        // ✅ ENHANCED formatting for Chinese lesson content
        String formatted = rawContent.trim()
                .replaceAll("\\r\\n", "\n")      // Normalize line breaks
                .replaceAll("\\r", "\n")         // Handle remaining \r
                .replaceAll("\\n{3,}", "\n\n")   // Limit consecutive line breaks to max 2
                .replaceAll("^\\s+", "")         // Remove leading whitespace
                .replaceAll("\\s+$", "");        // Remove trailing whitespace

        // ✅ OPTIONAL: Enhanced formatting for Chinese content
        formatted = formatted
                .replaceAll("(\\d+\\.)\\s*", "\n$1 ")  // Add space after numbers (1. 2. 3.)
                .replaceAll("([：:])\\s*", "$1\n")      // Add line break after colons
                .replaceAll("(-\\s*)([^\\n])", "  • $2") // Convert dashes to bullet points
                .replaceAll("\\n\\s*\\n", "\n\n")       // Clean up double line breaks
                .trim();

        return formatted;
    }

    // ✅ DEBUG: Add method to log content for debugging
    private void debugBaiGiangContent(BaiGiang baiGiang) {
        Log.d(TAG, "=== BAIGANG CONTENT DEBUG ===");
        Log.d(TAG, "Title: " + baiGiang.getTieuDe());
        Log.d(TAG, "MoTa (short): " + baiGiang.getMoTa());
        Log.d(TAG, "NoiDung (detailed) length: " + (baiGiang.getNoiDung() != null ? baiGiang.getNoiDung().length() : 0));
        Log.d(TAG, "NoiDung preview: " + (baiGiang.getNoiDung() != null ?
                baiGiang.getNoiDung().substring(0, Math.min(100, baiGiang.getNoiDung().length())) + "..." : "null"));
        Log.d(TAG, "===============================");
    }

    private void setupVideo(String videoUrl) {
        if (playerView == null) {
            Log.e(TAG, "PlayerView is null");
            showNoVideoMessage();
            return;
        }

        if (TextUtils.isEmpty(videoUrl)) {
            Log.d(TAG, "No video URL provided");
            showNoVideoMessage();
            return;
        }

        try {
            String finalVideoUrl = buildCorrectFileUrl(videoUrl);
            if (finalVideoUrl == null) {
                Log.e(TAG, "Failed to build video URL");
                showNoVideoMessage();
                return;
            }

            Log.d(TAG, "Setting up video with URL: " + finalVideoUrl);

            if (player != null) {
                player.release();
            }

            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(finalVideoUrl));
            player.setMediaItem(mediaItem);

            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                    Log.e(TAG, "Player error: " + error.getMessage(), error);
                    runOnUiThread(() -> {
                        Toast.makeText(BaiGiangDetailActivity.this,
                                "Lỗi phát video: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                        showNoVideoMessage();
                    });
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            Log.d(TAG, "Video đang tải...");
                            break;
                        case Player.STATE_READY:
                            Log.d(TAG, "Video sẵn sàng phát");
                            runOnUiThread(() -> {
                                playerView.setVisibility(View.VISIBLE);
                                if (tvNoVideo != null) {
                                    tvNoVideo.setVisibility(View.GONE);
                                }
                            });
                            break;
                        case Player.STATE_ENDED:
                            Log.d(TAG, "Video đã phát xong");
                            break;
                        case Player.STATE_IDLE:
                            Log.d(TAG, "Player ở trạng thái idle");
                            break;
                    }
                }
            });

            player.prepare();
            playerView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video: " + e.getMessage(), e);
            showNoVideoMessage();
        }
    }

    private String buildCorrectFileUrl(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return null;
        }

        // If already a full URL, return as is
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            return fileUrl;
        }

        String fileName = extractFileName(fileUrl);

        // ✅ FIXED: Use Constants.getCorrectVideoUrl() instead of hardcoded path
        return fileName != null ? Constants.getCorrectVideoUrl(fileName) : null;
    }

    // ✅ ALTERNATIVE FIX: Use the correct API endpoint directly
    private String buildCorrectFileUrlAlternative(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return null;
        }

        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            return fileUrl;
        }

        String fileName = extractFileName(fileUrl);

        // ✅ FIXED: Use correct video API endpoint
        return fileName != null ? Constants.getBaseUrl() + "api/media/video/" + fileName : null;
    }

    private String extractFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int lastSlash = filePath.lastIndexOf('/');
        return lastSlash != -1 ? filePath.substring(lastSlash + 1) : filePath;
    }

    private void showNoVideoMessage() {
        runOnUiThread(() -> {
            if (playerView != null) {
                playerView.setVisibility(View.GONE);
            }
            if (tvNoVideo != null) {
                tvNoVideo.setVisibility(View.VISIBLE);
            }
        });
    }


    private void updateTimeDisplay() {
        if (startTime > 0) {
            long sessionTime = System.currentTimeMillis() - startTime;
            totalWatchTime += sessionTime;
            startTime = System.currentTimeMillis();
        }
    }

    private void setupViewPager() {
        // Implement ViewPager setup for tabs if needed
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);

        runOnUiThread(() -> {
            showLoading(false);

            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(this)
                        .setTitle("Lỗi")
                        .setMessage("Đã xảy ra lỗi: " + message + "\n\nBạn có muốn thử lại?")
                        .setPositiveButton("Thử lại", (dialog, which) -> loadBaiGiang())
                        .setNegativeButton("Thoát", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (player != null) {
                player.release();
                player = null;
            }

            updateTimeDisplay();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage(), e);
        }
    }

    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        // Not used
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        showLoading(false);
        currentBaiGiang = baiGiang;
        displayBaiGiang(baiGiang);
//        setupViewPager();
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        // Not used
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        // Not used
    }

    @Override
    public void onBaiGiangDeleted() {
        // Not used
    }

    @Override
    public void onError(String message) {
        showError(message);
    }


}