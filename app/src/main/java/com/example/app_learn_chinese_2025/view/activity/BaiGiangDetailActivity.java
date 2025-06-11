package com.example.app_learn_chinese_2025.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        // Get baiGiangId from intent extras
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
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

        // Load data
        loadBaiGiang();
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

    private void loadBaiGiang() {
        showLoading(true);
        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                showLoading(false);
                currentBaiGiang = baiGiang;
                displayBaiGiang(baiGiang);
                setupViewPager();
                loadTienTrinh();
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
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

            // Load hình ảnh
            if (baiGiang.getHinhAnh() != null && !baiGiang.getHinhAnh().isEmpty()) {
                String imageUrl = Constants.BASE_URL + baiGiang.getHinhAnh();
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(ivBaiGiang);
            }

            // Load video nếu có
            if (baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty()) {
                String videoUrl = Constants.BASE_URL + baiGiang.getVideoURL();
                playerView.setVisibility(View.VISIBLE);
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                player.setMediaItem(mediaItem);
                player.prepare();
            } else {
                playerView.setVisibility(View.GONE);
            }
        }
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
                fabMarkComplete.setImageResource(android.R.drawable.ic_menu_upload);
                fabMarkComplete.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_dark));
            } else {
                fabMarkComplete.setImageResource(android.R.drawable.ic_media_play);
                fabMarkComplete.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_blue_dark));
            }

            // Update time display
            updateTimeDisplay();
        }
    }

    private void updateTimeDisplay() {
        if (tvTimeSpent != null) {
            long timeSpent = System.currentTimeMillis() - startTime;
            int minutes = (int) (timeSpent / 60000);
            tvTimeSpent.setText("Thời gian học: " + minutes + " phút");
        }
    }

    private void updateLearningProgress(int currentTab) {
        if (currentTienTrinh == null || currentTienTrinh.isDaHoanThanh()) {
            return;
        }

        // Calculate progress based on tabs visited
        int baseProgress = (currentTab + 1) * 25; // 25% per tab

        // Add bonus for time spent
        long timeSpent = System.currentTimeMillis() - startTime;
        int timeBonus = Math.min((int)(timeSpent / 60000), 25); // 1% per minute, max 25%

        int newProgress = Math.min(baseProgress + timeBonus, 95); // Max 95% until marked complete

        if (newProgress > currentTienTrinh.getTienDo()) {
            currentTienTrinh.setTienDo(newProgress);
            currentTienTrinh.updateTrangThaiFromTienDo(); // FIXED: Cập nhật trạng thái
            currentTienTrinh.setNgayCapNhat(new Date());

            // Save progress
            tienTrinhRepository.saveTienTrinh(currentTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
                @Override
                public void onSuccess(TienTrinh tienTrinh) {
                    currentTienTrinh = tienTrinh;
                    updateProgressUI();
                }

                @Override
                public void onError(String errorMessage) {
                    // Ignore save errors for progress updates
                }
            });
        }
    }

    private void markLessonAsCompleted() {
        if (currentTienTrinh == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hoàn thành bài học")
                .setMessage("Bạn đã hoàn thành bài học này chưa?\n\nViệc đánh dấu hoàn thành sẽ cập nhật tiến độ học tập của bạn.")
                .setPositiveButton("Hoàn thành", (dialog, which) -> {
                    // FIXED: Cập nhật đúng cách
                    currentTienTrinh.setTienDo(100);
                    currentTienTrinh.setDaHoanThanh(true);
                    currentTienTrinh.setTrangThai(2); // Đã hoàn thành
                    currentTienTrinh.setNgayHoanThanh(new Date());
                    currentTienTrinh.setNgayCapNhat(new Date());

                    tienTrinhRepository.saveTienTrinh(currentTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
                        @Override
                        public void onSuccess(TienTrinh tienTrinh) {
                            currentTienTrinh = tienTrinh;
                            updateProgressUI();
                            Toast.makeText(BaiGiangDetailActivity.this, "🎉 Chúc mừng! Bạn đã hoàn thành bài học", Toast.LENGTH_LONG).show();
                            showCompletionDialog();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(BaiGiangDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Chưa xong", null)
                .show();
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Xuất sắc!")
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
            player = null;
        }
    }

    // Implement các phương thức của OnBaiGiangListener
    @Override
    public void onBaiGiangLoaded(BaiGiang baiGiang) {
        displayBaiGiang(baiGiang);
    }

    @Override
    public void onBaiGiangError(String error) {
        Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTienTrinhLoaded(TienTrinh tienTrinh) {
        if (tienTrinh != null) {
            int progress = (int) ((tienTrinh.getThoiGianHoc() * 100) / (baiGiangController.getBaiGiang().getThoiLuong() * 60));
            tvProgress.setText("Tiến độ: " + progress + "%");
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onTienTrinhError(String error) {
        Toast.makeText(this, "Lỗi cập nhật tiến độ: " + error, Toast.LENGTH_SHORT).show();
    }
}