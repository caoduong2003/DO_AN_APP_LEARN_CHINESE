package com.example.app_learn_chinese_2025.view.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.model.repository.TienTrinhRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.ViewPagerAdapter;
import com.example.app_learn_chinese_2025.view.fragment.MauCauListFragment;
import com.example.app_learn_chinese_2025.view.fragment.NoiDungBaiGiangFragment;
import com.example.app_learn_chinese_2025.view.fragment.TuVungListFragment;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BaiGiangDetailActivity extends AppCompatActivity implements BaiGiangController.OnBaiGiangListener {
    private long baiGiangId;

    private Toolbar toolbar;
    private ImageView ivBaiGiang;
    private PlayerView playerView;
    private TextView tvTieuDe, tvMoTa, tvNoiDung, tvThoiLuong, tvLuotXem, tvProgress;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabMarkComplete;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private TienTrinhRepository tienTrinhRepository;
    private BaiGiang currentBaiGiang;
    private TienTrinh currentTienTrinh;
    private List<Fragment> fragmentList;
    private ViewPagerAdapter viewPagerAdapter;
    private long startTime; // Track learning time
    private ExoPlayer player;
    private BaiGiangController baiGiangController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai_giang_detail);

        // Record start time for learning session
        startTime = System.currentTimeMillis();

        // Debug: Kiểm tra BASE_URL
        Log.d("DEBUG_VIDEO", "BASE_URL: " + Constants.BASE_URL);

        // Get baiGiangId from intent extras
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
            Log.d("DEBUG_VIDEO", "BAI_GIANG_ID: " + baiGiangId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo controller và session manager
        baiGiangController = new BaiGiangController(this, this);
        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        setupListeners();
        initExoPlayer();

        // Kiểm tra network ngay khi vào activity
        checkNetworkAndVideo();

        // Load data
        if (baiGiangId != -1) {
            loadBaiGiang();
        } else {
            Toast.makeText(this, "Không tìm thấy bài giảng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBaiGiang = findViewById(R.id.ivBaiGiang);
        playerView = findViewById(R.id.playerView);
        tvTieuDe = findViewById(R.id.tvTieuDe);
        tvMoTa = findViewById(R.id.tvMoTa);
        tvNoiDung = findViewById(R.id.tvNoiDung);
        tvThoiLuong = findViewById(R.id.tvThoiLuong);
        tvLuotXem = findViewById(R.id.tvLuotXem);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabMarkComplete = findViewById(R.id.fabMarkComplete);

        sessionManager = new SessionManager(this);
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        tienTrinhRepository = new TienTrinhRepository(sessionManager);
        fragmentList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi tiết bài giảng");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bai_giang_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_bookmark) {
            Toast.makeText(this, "Tính năng đánh dấu đang phát triển", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_share) {
            shareLesson();
            return true;
        } else if (id == R.id.action_notes) {
            Toast.makeText(this, "Tính năng ghi chú đang phát triển", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        // Create and pass baiGiangId to fragments
        Bundle bundle = new Bundle();
        bundle.putLong("BAI_GIANG_ID", baiGiangId);

        NoiDungBaiGiangFragment noiDungFragment = new NoiDungBaiGiangFragment();
        noiDungFragment.setArguments(bundle);

        TuVungListFragment tuVungFragment = new TuVungListFragment();
        tuVungFragment.setArguments(bundle);

        MauCauListFragment mauCauFragment = new MauCauListFragment();
        mauCauFragment.setArguments(bundle);

        // Add fragments
        fragmentList.add(noiDungFragment);
        fragmentList.add(tuVungFragment);
        fragmentList.add(mauCauFragment);

        // Setup ViewPager
        viewPagerAdapter = new ViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        // Connect TabLayout with ViewPager2
        String[] tabTitles = {"Nội dung", "Từ vựng", "Mẫu câu"};

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        // Track tab changes for progress
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateLearningProgress(position);
            }
        });
    }

    private void setupListeners() {
        fabMarkComplete.setOnClickListener(v -> {
            if (currentTienTrinh != null && currentTienTrinh.isDaHoanThanh()) {
                showCompletionDialog();
            } else {
                markLessonAsCompleted();
            }
        });
    }

    private void initExoPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
    }

    // KIỂM TRA NETWORK VÀ SERVER CONNECTION
    private void checkNetworkAndVideo() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        Log.d("DEBUG_VIDEO", "Network connected: " + isConnected);

        if (!isConnected) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }

        // Test ping đến server
        testServerConnection();
    }

    private void testServerConnection() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.BASE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();

                runOnUiThread(() -> {
                    Log.d("DEBUG_VIDEO", "Server response code: " + responseCode);
                    if (responseCode == 200) {
                        Log.d("DEBUG_VIDEO", "Kết nối server thành công");
                    } else {
                        Toast.makeText(this, "Server trả về lỗi: " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("DEBUG_VIDEO", "Lỗi kết nối server: " + e.getMessage());
                    Toast.makeText(this, "Không thể kết nối server: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadBaiGiang() {
        showLoading(true);
        Log.d("DEBUG_VIDEO", "Bắt đầu load bài giảng với ID: " + baiGiangId);

        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                showLoading(false);
                Log.d("DEBUG_VIDEO", "Load bài giảng thành công");
                Log.d("DEBUG_VIDEO", "Video URL from API: " + baiGiang.getVideoURL());
                Log.d("DEBUG_VIDEO", "Image URL from API: " + baiGiang.getHinhAnh());
                Log.d("DEBUG_VIDEO", "Bài giảng: " + baiGiang.getTieuDe());

                currentBaiGiang = baiGiang;
                displayBaiGiang(baiGiang);
                setupViewPager();
                loadTienTrinh();
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                Log.e("DEBUG_VIDEO", "Lỗi load bài giảng: " + errorMessage);
                Toast.makeText(BaiGiangDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayBaiGiang(BaiGiang baiGiang) {
        if (baiGiang != null) {
            getSupportActionBar().setTitle(baiGiang.getTieuDe());

            // Hiển thị thông tin cơ bản
            tvTieuDe.setText(baiGiang.getTieuDe());
            tvMoTa.setText(baiGiang.getMoTa());
            tvNoiDung.setText(baiGiang.getNoiDung());
            tvThoiLuong.setText(formatThoiLuong(baiGiang.getThoiLuong()));
            tvLuotXem.setText(baiGiang.getLuotXem() + " lượt xem");

            // Load hình ảnh với URL đúng
            if (baiGiang.getHinhAnh() != null && !baiGiang.getHinhAnh().isEmpty()) {
                String imageUrl = buildCorrectFileUrl(baiGiang.getHinhAnh());
                Log.d("IMAGE_DEBUG", "Loading image: " + imageUrl);

                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e("IMAGE_DEBUG", "Failed to load image: " + imageUrl);
                                if (e != null) {
                                    Log.e("IMAGE_DEBUG", "Glide error: " + e.getMessage());
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                Log.d("IMAGE_DEBUG", "Image loaded successfully: " + imageUrl);
                                return false;
                            }
                        })
                        .into(ivBaiGiang);
            }

            // Load video nếu có
            if (baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty()) {
                setupVideoPlayer(baiGiang.getVideoURL());
            } else {
                playerView.setVisibility(View.GONE);
                showNoVideoMessage();
            }
        }
    }

    private void setupVideoPlayer(String videoUrl) {
        try {
            // Xây dựng URL đúng theo cách server serve file
            String fullVideoUrl = buildCorrectFileUrl(videoUrl);

            Log.d("VIDEO_DEBUG", "Original URL: " + videoUrl);
            Log.d("VIDEO_DEBUG", "Correct URL: " + fullVideoUrl);

            // Test URL trước khi load vào player
            testVideoUrl(fullVideoUrl);

            playerView.setVisibility(View.VISIBLE);

            // Tạo MediaItem từ URL
            MediaItem mediaItem = MediaItem.fromUri(fullVideoUrl);
            player.setMediaItem(mediaItem);

            // Thêm listener để xử lý các sự kiện
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e("VIDEO_ERROR", "Playback error: " + error.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(BaiGiangDetailActivity.this,
                                "Không thể phát video. Vui lòng thử lại sau.",
                                Toast.LENGTH_LONG).show();
                        playerView.setVisibility(View.GONE);
                        showNoVideoMessage();
                    });
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            Log.d("VIDEO_DEBUG", "Video đang tải...");
                            break;
                        case Player.STATE_READY:
                            Log.d("VIDEO_DEBUG", "Video sẵn sàng phát");
                            Toast.makeText(BaiGiangDetailActivity.this,
                                    "Video đã sẵn sàng", Toast.LENGTH_SHORT).show();
                            break;
                        case Player.STATE_ENDED:
                            Log.d("VIDEO_DEBUG", "Video đã phát xong");
                            break;
                        case Player.STATE_IDLE:
                            Log.d("VIDEO_DEBUG", "Player ở trạng thái idle");
                            break;
                    }
                }
            });

            player.prepare();

        } catch (Exception e) {
            Log.e("VIDEO_ERROR", "Lỗi khi setup video: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tải video: " + e.getMessage(), Toast.LENGTH_LONG).show();
            playerView.setVisibility(View.GONE);
            showNoVideoMessage();
        }
    }

    /**
     * Xây dựng URL đúng để truy cập file từ server
     * Server serve file tại /api/files/{fileName}
     */
    private String buildCorrectFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            return fileUrl;
        }

        // Lấy tên file từ đường dẫn
        String fileName = extractFileName(fileUrl);

        // Xây dựng URL đúng theo cách server serve file
        return Constants.BASE_URL + "api/files/" + fileName;
    }

    /**
     * Trích xuất tên file từ đường dẫn
     */
    private String extractFileName(String filePath) {
        if (filePath.startsWith("/uploads/videos/")) {
            return filePath.substring("/uploads/videos/".length());
        } else if (filePath.startsWith("/uploads/images/")) {
            return filePath.substring("/uploads/images/".length());
        } else if (filePath.startsWith("uploads/videos/")) {
            return filePath.substring("uploads/videos/".length());
        } else if (filePath.startsWith("uploads/images/")) {
            return filePath.substring("uploads/images/".length());
        } else if (filePath.contains("/")) {
            // Lấy phần sau dấu / cuối cùng
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        } else {
            // Đã là tên file
            return filePath;
        }
    }

    private void showNoVideoMessage() {
        // Hiển thị thông báo không có video một cách đơn giản
        Toast.makeText(this, "Bài giảng này chưa có video", Toast.LENGTH_SHORT).show();
    }

    private void testVideoUrl(String url) {
        // Test URL bằng cách ping đến server
        new Thread(() -> {
            try {
                java.net.URL testUrl = new java.net.URL(url);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) testUrl.openConnection();
                connection.setRequestMethod("HEAD"); // Chỉ lấy header
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                String contentType = connection.getContentType();

                runOnUiThread(() -> {
                    Log.d("VIDEO_TEST", "URL: " + url);
                    Log.d("VIDEO_TEST", "Response code: " + responseCode);
                    Log.d("VIDEO_TEST", "Content-Type: " + contentType);

                    if (responseCode == 200) {
                        Log.d("VIDEO_TEST", "URL accessible!");
                        if (contentType != null && contentType.startsWith("video/")) {
                            Log.d("VIDEO_TEST", "Content type is video - OK!");
                        } else {
                            Log.w("VIDEO_TEST", "Content type is not video: " + contentType);
                        }
                    } else {
                        Log.e("VIDEO_TEST", "URL not accessible, response code: " + responseCode);
                        Toast.makeText(this, "Không thể truy cập video (Lỗi " + responseCode + ")",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("VIDEO_TEST", "Error testing URL: " + e.getMessage());
                    Toast.makeText(this, "Lỗi kiểm tra video: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String formatThoiLuong(int phut) {
        if (phut < 60) {
            return phut + " phút";
        } else {
            int gio = phut / 60;
            int phutConLai = phut % 60;
            return gio + " giờ " + phutConLai + " phút";
        }
    }

    private void loadTienTrinh() {
        User user = sessionManager.getUserDetails();
        if (user == null) {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        tienTrinhRepository.getTienTrinhByUserAndBaiGiang(user.getID(), baiGiangId, new TienTrinhRepository.OnTienTrinhCallback() {
            @Override
            public void onSuccess(TienTrinh tienTrinh) {
                currentTienTrinh = tienTrinh;

                // FIXED: Kiểm tra và tạo TienTrinh mới nếu cần
                if (tienTrinh == null || tienTrinh.getId() == 0) {
                    createNewTienTrinh(user);
                } else {
                    updateProgressUI();
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Nếu không tìm thấy tiến trình, tạo mới
                createNewTienTrinh(user);
            }
        });
    }

    private void createNewTienTrinh(User user) {
        if (currentBaiGiang == null || user == null) {
            return;
        }

        TienTrinh newTienTrinh = new TienTrinh();
        newTienTrinh.setUser(user);
        newTienTrinh.setBaiGiang(currentBaiGiang);
        newTienTrinh.setTienDo(0);
        newTienTrinh.setDaHoanThanh(false);
        newTienTrinh.setNgayBatDau(new Date());

        tienTrinhRepository.saveTienTrinh(newTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
            @Override
            public void onSuccess(TienTrinh savedTienTrinh) {
                currentTienTrinh = savedTienTrinh;
                updateProgressUI();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BaiGiangDetailActivity.this, "Không thể tạo tiến trình: " + errorMessage, Toast.LENGTH_SHORT).show();
                // Vẫn tạo object local để app không crash
                currentTienTrinh = newTienTrinh;
                updateProgressUI();
            }
        });
    }

    private void updateProgressUI() {
        if (currentTienTrinh != null) {
            int progress = currentTienTrinh.getTienDo();
            progressBar.setProgress(progress);
            tvProgress.setText("Tiến độ: " + progress + "%");

            // Update completion status
            if (currentTienTrinh.isDaHoanThanh()) {
                fabMarkComplete.setImageResource(R.drawable.ic_check_circle);
                fabMarkComplete.setContentDescription("Đã hoàn thành");
            } else {
                fabMarkComplete.setImageResource(R.drawable.bg_button_primary);
                fabMarkComplete.setContentDescription("Đánh dấu hoàn thành");
            }
        }
    }

    private void updateLearningProgress(int tabPosition) {
        if (currentTienTrinh == null || currentTienTrinh.isDaHoanThanh()) {
            return;
        }

        // Calculate progress based on tab visited (each tab = 33.33%)
        int newProgress = Math.max(currentTienTrinh.getTienDo(), (tabPosition + 1) * 33);

        if (newProgress > currentTienTrinh.getTienDo()) {
            currentTienTrinh.setTienDo(newProgress);
            currentTienTrinh.setNgayCapNhat(new Date());

            // Update learning time
            updateTimeDisplay();

            // Save progress
            saveTienTrinh();
        }
    }

    private void updateTimeDisplay() {
        if (currentTienTrinh != null) {
            long currentTime = System.currentTimeMillis();
            long sessionTime = (currentTime - startTime) / 1000 / 60; // minutes

            // Cập nhật ngày cập nhật để theo dõi thời gian học
            currentTienTrinh.setNgayCapNhat(new Date());

            // Reset start time for next session tracking
            startTime = currentTime;
        }
    }

    private void saveTienTrinh() {
        if (currentTienTrinh != null) {
            tienTrinhRepository.saveTienTrinh(currentTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
                @Override
                public void onSuccess(TienTrinh savedTienTrinh) {
                    currentTienTrinh = savedTienTrinh;
                    updateProgressUI();
                }

                @Override
                public void onError(String errorMessage) {
                    // Silent fail - progress still tracked locally
                }
            });
        }
    }

    private void markLessonAsCompleted() {
        if (currentTienTrinh == null) {
            return;
        }

        currentTienTrinh.setTienDo(100);
        currentTienTrinh.setDaHoanThanh(true);
        currentTienTrinh.setNgayHoanThanh(new Date());
        currentTienTrinh.setNgayCapNhat(new Date());

        // Update learning time
        updateTimeDisplay();

        tienTrinhRepository.saveTienTrinh(currentTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
            @Override
            public void onSuccess(TienTrinh savedTienTrinh) {
                currentTienTrinh = savedTienTrinh;
                updateProgressUI();
                Toast.makeText(BaiGiangDetailActivity.this, "Chúc mừng! Bạn đã hoàn thành bài học này!", Toast.LENGTH_LONG).show();
                showCompletionDialog();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BaiGiangDetailActivity.this, "Không thể cập nhật tiến độ: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hoàn thành bài học!")
                .setMessage("Bạn đã hoàn thành bài học này!\n\nBạn có muốn:")
                .setPositiveButton("Học bài tiếp theo", (dialog, which) -> {
                    Toast.makeText(this, "Tính năng này đang phát triển", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Ôn tập lại", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNeutralButton("Về trang chủ", (dialog, which) -> {
                    finish();
                })
                .show();
    }

    private void shareLesson() {
        if (currentBaiGiang != null) {
            String shareText = "Tôi đang học bài: " + currentBaiGiang.getTieuDe() +
                    "\nỨng dụng học tiếng Trung - Chinese Learning App";

            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "Chia sẻ bài học"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateTimeDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        updateTimeDisplay();
    }

    @Override
    public void onBackPressed() {
        if (currentTienTrinh != null && !currentTienTrinh.isDaHoanThanh() && currentTienTrinh.getTienDo() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Lưu tiến độ")
                    .setMessage("Tiến độ học tập của bạn đã được lưu tự động.\nBạn có thể tiếp tục học bất cứ lúc nào!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        super.onBackPressed();
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
        // Update final learning time before closing
        updateTimeDisplay();
        saveTienTrinh();
    }

    // Implement BaiGiangController.OnBaiGiangListener methods
    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        // This method is not used in detail activity
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        showLoading(false);
        currentBaiGiang = baiGiang;
        displayBaiGiang(baiGiang);
        setupViewPager();
        loadTienTrinh();
    }

    @Override
    public void onError(String message) {
        showLoading(false);
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();

        // Log error for debugging
        android.util.Log.e("BaiGiangDetailActivity", "Error loading BaiGiang: " + message);

        // Thêm dialog để retry
        new AlertDialog.Builder(this)
                .setTitle("Lỗi kết nối")
                .setMessage("Không thể tải dữ liệu bài giảng.\n\nLỗi: " + message + "\n\nBạn có muốn thử lại?")
                .setPositiveButton("Thử lại", (dialog, which) -> {
                    loadBaiGiang();
                })
                .setNegativeButton("Thoát", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}