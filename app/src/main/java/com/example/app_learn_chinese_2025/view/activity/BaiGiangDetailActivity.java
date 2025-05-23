package com.example.app_learn_chinese_2025.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaiGiangDetailActivity extends AppCompatActivity {
    private long baiGiangId;

    private Toolbar toolbar;
    private ImageView ivBaiGiang;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bai_giang_detail);

        // Get baiGiangId from intent extras
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupListeners();

        // Load data
        loadBaiGiang();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBaiGiang = findViewById(R.id.ivBaiGiang);
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
    }

    private void setupListeners() {
        fabMarkComplete.setOnClickListener(v -> markLessonAsCompleted());
    }

    private void loadBaiGiang() {
        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;

                // Update UI with baiGiang data
                updateUI();

                // Setup ViewPager after loading baiGiang
                setupViewPager();

                // Load user's progress for this lesson
                loadTienTrinh();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BaiGiangDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        if (currentBaiGiang != null) {
            // Set title
            getSupportActionBar().setTitle(currentBaiGiang.getTieuDe());

            // Load image
            if (currentBaiGiang.getHinhAnh() != null && !currentBaiGiang.getHinhAnh().isEmpty()) {
                String imageUrl = Constants.BASE_URL + currentBaiGiang.getHinhAnh();
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(ivBaiGiang);
            }

            // Update the lesson's view count through API
            // This is normally handled by the server when getBaiGiangById is called
        }
    }

    private void loadTienTrinh() {
        // Get current user ID
        User user = sessionManager.getUserDetails();
        if (user == null) {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        tienTrinhRepository.getTienTrinhByUserAndBaiGiang(user.getID(), baiGiangId, new TienTrinhRepository.OnTienTrinhCallback() {
            @Override
            public void onSuccess(TienTrinh tienTrinh) {
                currentTienTrinh = tienTrinh;

                // Update FAB based on completion status
                updateFAB();

                // If this is a new TienTrinh (no ID), create it
                if (tienTrinh.getId() == 0) {
                    createNewTienTrinh(user);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BaiGiangDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewTienTrinh(User user) {
        // Create a new progress record
        TienTrinh newTienTrinh = new TienTrinh();
        newTienTrinh.setUser(user);
        newTienTrinh.setBaiGiang(currentBaiGiang);
        newTienTrinh.setTienDo(0);
        newTienTrinh.setDaHoanThanh(false);
        newTienTrinh.setNgayBatDau(new Date());
        newTienTrinh.setNgayCapNhat(new Date());

        tienTrinhRepository.saveTienTrinh(newTienTrinh, new TienTrinhRepository.OnTienTrinhCallback() {
            @Override
            public void onSuccess(TienTrinh savedTienTrinh) {
                currentTienTrinh = savedTienTrinh;

                // Update UI
                updateFAB();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BaiGiangDetailActivity.this, "Không thể tạo tiến trình: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFAB() {
        if (currentTienTrinh != null && currentTienTrinh.isDaHoanThanh()) {
            // Lesson already completed
            fabMarkComplete.setImageResource(android.R.drawable.ic_menu_check);
            fabMarkComplete.setOnClickListener(v -> {
                Toast.makeText(this, "Bài giảng đã hoàn thành", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Lesson not completed
            fabMarkComplete.setImageResource(android.R.drawable.ic_menu_add);
            fabMarkComplete.setOnClickListener(v -> markLessonAsCompleted());
        }
    }

    private void markLessonAsCompleted() {
        if (currentTienTrinh == null || currentTienTrinh.isDaHoanThanh()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hoàn thành")
                .setMessage("Bạn có chắc chắn muốn đánh dấu bài giảng này là đã hoàn thành?")
                .setPositiveButton("Có", (dialog, which) -> {
                    if (currentTienTrinh.getId() > 0) {
                        // Mark existing progress as completed
                        tienTrinhRepository.markAsCompleted(currentTienTrinh.getId(), new TienTrinhRepository.OnTienTrinhCallback() {
                            @Override
                            public void onSuccess(TienTrinh tienTrinh) {
                                currentTienTrinh = tienTrinh;

                                Toast.makeText(BaiGiangDetailActivity.this, "Đã đánh dấu hoàn thành", Toast.LENGTH_SHORT).show();

                                // Update UI
                                updateFAB();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(BaiGiangDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Không thể đánh dấu hoàn thành", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}