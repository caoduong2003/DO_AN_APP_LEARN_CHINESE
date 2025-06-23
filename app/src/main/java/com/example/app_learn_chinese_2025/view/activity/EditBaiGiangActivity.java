package com.example.app_learn_chinese_2025.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.Date;
import java.util.List;

public class EditBaiGiangActivity extends AppCompatActivity implements BaiGiangController.OnBaiGiangListener {
    private static final String TAG = "EditBaiGiangActivity";

    private Toolbar toolbar;
    private EditText etTitle, etDescription, etVideoUrl, etThumbnailUrl;
    private CheckBox cbPremium, cbPublished;
    private Button btnSave;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private BaiGiangController baiGiangController;
    private long baiGiangId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (sessionManager.getUserRole() != Constants.ROLE_TEACHER) {
            Toast.makeText(this, "Chỉ giảng viên có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_edit_bai_giang);

        initViews();
        setupToolbar();
        setupListeners();
        loadBaiGiangData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        etThumbnailUrl = findViewById(R.id.etThumbnailUrl);
        cbPremium = findViewById(R.id.cbPremium);
        cbPublished = findViewById(R.id.cbPublished);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        baiGiangController = new BaiGiangController(this, this);

        // Kiểm tra chế độ chỉnh sửa
        Intent intent = getIntent();
        if (intent.hasExtra("BAI_GIANG_ID")) {
            baiGiangId = intent.getLongExtra("BAI_GIANG_ID", -1);
            isEditMode = true;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(isEditMode ? "Chỉnh sửa bài giảng" : "Thêm bài giảng");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveBaiGiang());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadBaiGiangData() {
        if (isEditMode && baiGiangId != -1) {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            baiGiangController.getBaiGiangDetail(baiGiangId);
        }
    }

    private void saveBaiGiang() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String videoUrl = etVideoUrl.getText().toString().trim();
        String thumbnailUrl = etThumbnailUrl.getText().toString().trim();
        boolean isPremium = cbPremium.isChecked();
        boolean isPublished = cbPublished.isChecked();

        // Validate input
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            etTitle.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        BaiGiang baiGiang = new BaiGiang();
        baiGiang.setTieuDe(title);
        baiGiang.setMoTa(description);
        baiGiang.setVideoURL(videoUrl);
        baiGiang.setThumbnailURL(thumbnailUrl);
        baiGiang.setLaBaiGiangGoi(isPremium);
        baiGiang.setPublished(isPublished);
        baiGiang.setGiangVien(sessionManager.getUserDetails());
        baiGiang.setNgayTao(isEditMode ? baiGiang.getNgayTao() : new Date());
        baiGiang.setNgayCapNhat(new Date());

        if (isEditMode) {
            baiGiang.setID(baiGiangId);
            baiGiangController.updateBaiGiang(baiGiangId, baiGiang);
        } else {
            baiGiangController.createBaiGiang(baiGiang);
        }
    }

    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {}

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        progressBar.setVisibility(View.GONE);
        etTitle.setText(baiGiang.getTieuDe());
        etDescription.setText(baiGiang.getMoTa());
        etVideoUrl.setText(baiGiang.getVideoURL());
        etThumbnailUrl.setText(baiGiang.getThumbnailURL());
        cbPremium.setChecked(baiGiang.isLaBaiGiangGoi());
        cbPublished.setChecked(baiGiang.isPublished());
        Log.d(TAG, "Loaded bài giảng: " + baiGiang.getTieuDe());
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        setResult(RESULT_OK);
        finish();
        Log.d(TAG, "Created bài giảng: " + baiGiang.getTieuDe());
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        setResult(RESULT_OK);
        finish();
        Log.d(TAG, "Updated bài giảng: " + baiGiang.getTieuDe());
    }

    @Override
    public void onBaiGiangDeleted() {}

    @Override
    public void onError(String message) {
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
    }
}