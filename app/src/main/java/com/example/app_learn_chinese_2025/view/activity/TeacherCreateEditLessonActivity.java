package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.TeacherBaiGiangController;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import com.example.app_learn_chinese_2025.util.DropdownDataProvider;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.ChuDe;

import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 Activity tạo mới hoặc chỉnh sửa bài giảng
 * Hỗ trợ đầy đủ chức năng CRUD cho giáo viên
 */
public class TeacherCreateEditLessonActivity extends AppCompatActivity
        implements TeacherBaiGiangController.OnTeacherBaiGiangListener {

    private static final String TAG = "TeacherCreateEditLesson";
    private static final int REQUEST_SELECT_IMAGE = 1001;

    // UI Components
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    // Form Fields
    private TextInputEditText etTitle, etDescription, etDuration, etVideoUrl, etAudioUrl, etContent;
    private AutoCompleteTextView spinnerHSKLevel, spinnerTopic, spinnerLessonType;
    private SwitchMaterial switchPublicStatus, switchPremiumStatus;
    private ImageView ivThumbnail;
    private TextView tvThumbnailInfo;
    private Button btnSelectThumbnail, btnCancel, btnSave;

    // Data & Controllers
    private SessionManager sessionManager;
    private TeacherBaiGiangController controller;

    // Mode & Data
    private boolean isEditMode = false;
    private Long lessonId = null;
    private ApiService.TeacherBaiGiangResponse.DetailResponse currentLesson;
    private String selectedImagePath;

    // Dropdown Data
    private List<String> hskLevels = new ArrayList<>();
    private List<String> topics = new ArrayList<>();
    private List<String> lessonTypes = new ArrayList<>();
    // Selected objects from dropdowns
    private CapDoHSK selectedCapDoHSK;
    private LoaiBaiGiang selectedLoaiBaiGiang;
    private ChuDe selectedChuDe;

    // Dropdown data lists
    private List<CapDoHSK> capDoHSKList = new ArrayList<>();
    private List<LoaiBaiGiang> loaiBaiGiangList = new ArrayList<>();
    private List<ChuDe> chuDeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_edit_lesson);

        Log.d(TAG, "🚀 onCreate started");

        // Check teacher role
        sessionManager = new SessionManager(this);
        if (sessionManager.getUserRole() != Constants.ROLE_TEACHER) {
            Toast.makeText(this, "Chỉ giảng viên có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getIntentData();
        initViews();
        setupToolbar();
        setupDropdowns();
        setupListeners();

        if (isEditMode && lessonId != null) {
            loadLessonDetails();
        } else {
            setupDefaultValues();
        }
    }

    // ===== INITIALIZATION METHODS =====

    private void getIntentData() {
        if (getIntent().hasExtra("LESSON_ID")) {
            lessonId = getIntent().getLongExtra("LESSON_ID", -1);
            if (lessonId != -1) {
                isEditMode = true;
            }
        }

        if (getIntent().hasExtra("IS_EDIT_MODE")) {
            isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);
        }

        Log.d(TAG, "Mode: " + (isEditMode ? "EDIT" : "CREATE") + ", ID: " + lessonId);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);

        // Form fields
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDuration = findViewById(R.id.etDuration);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        etAudioUrl = findViewById(R.id.etAudioUrl);
        etContent = findViewById(R.id.etContent);

        // Dropdowns
        spinnerHSKLevel = findViewById(R.id.spinnerHSKLevel);
        spinnerTopic = findViewById(R.id.spinnerTopic);
        spinnerLessonType = findViewById(R.id.spinnerLessonType);

        // Switches
        switchPublicStatus = findViewById(R.id.switchPublicStatus);
        switchPremiumStatus = findViewById(R.id.switchPremiumStatus);

        // Image
        ivThumbnail = findViewById(R.id.ivThumbnail);
        tvThumbnailInfo = findViewById(R.id.tvThumbnailInfo);
        btnSelectThumbnail = findViewById(R.id.btnSelectThumbnail);

        // Buttons
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        // Initialize controllers
        controller = new TeacherBaiGiangController(this, this);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Chỉnh sửa bài giảng" : "Tạo bài giảng mới");
        }
    }

    private void setupDropdowns() {
        // HSK Levels - Lấy data và lưu trữ
        capDoHSKList = DropdownDataProvider.getCapDoHSKList();
        ArrayAdapter<CapDoHSK> hskAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, capDoHSKList);
        spinnerHSKLevel.setAdapter(hskAdapter);

        // Lesson Types - Lấy data và lưu trữ
        loaiBaiGiangList = DropdownDataProvider.getLoaiBaiGiangList();
        ArrayAdapter<LoaiBaiGiang> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, loaiBaiGiangList);
        spinnerLessonType.setAdapter(typeAdapter);

        // Topics - Lấy data và lưu trữ
        chuDeList = DropdownDataProvider.getChuDeList();
        ArrayAdapter<ChuDe> topicAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, chuDeList);
        spinnerTopic.setAdapter(topicAdapter);

        Log.d(TAG, "✅ Dropdowns setup completed - HSK: " + capDoHSKList.size() +
                ", Types: " + loaiBaiGiangList.size() + ", Topics: " + chuDeList.size());
    }

    private void setupListeners() {
        // Image selection
        btnSelectThumbnail.setOnClickListener(v -> selectImage());

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedChanges()) {
                showDiscardChangesDialog();
            } else {
                finish();
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> saveLessonData());

        Log.d(TAG, "✅ Listeners setup completed");

        // HSK Level selection listener
        spinnerHSKLevel.setOnItemClickListener((parent, view, position, id) -> {
            selectedCapDoHSK = capDoHSKList.get(position);
            Log.d(TAG, "Selected HSK Level: " + selectedCapDoHSK.getTenCapDo());
        });

        // Lesson Type selection listener
        spinnerLessonType.setOnItemClickListener((parent, view, position, id) -> {
            selectedLoaiBaiGiang = loaiBaiGiangList.get(position);
            Log.d(TAG, "Selected Lesson Type: " + selectedLoaiBaiGiang.getTenLoai());
        });

        // Topic selection listener
        spinnerTopic.setOnItemClickListener((parent, view, position, id) -> {
            selectedChuDe = chuDeList.get(position);
            Log.d(TAG, "Selected Topic: " + selectedChuDe.getTenChuDe());
        });
    }

    private void setupDefaultValues() {
        // Set default values for new lesson
        switchPublicStatus.setChecked(true);
        switchPremiumStatus.setChecked(false);
        etDuration.setText("15");

        Log.d(TAG, "✅ Default values set");
    }

    // ===== DATA LOADING =====

    private void loadLessonDetails() {
        Log.d(TAG, "🌐 Loading lesson details for ID: " + lessonId);
        showProgress(true);
        controller.getBaiGiangDetail(lessonId);
    }

    private void populateFormData(ApiService.TeacherBaiGiangResponse.DetailResponse lesson) {
        Log.d(TAG, "📝 Populating form with lesson data: " + lesson.getTieuDe());

        currentLesson = lesson;

        // Basic info
        etTitle.setText(lesson.getTieuDe() != null ? lesson.getTieuDe() : "");
        etDescription.setText(lesson.getMoTa() != null ? lesson.getMoTa() : "");
        etContent.setText(lesson.getNoiDung() != null ? lesson.getNoiDung() : "");

        // Duration
        if (lesson.getThoiLuong() != null) {
            etDuration.setText(String.valueOf(lesson.getThoiLuong()));
        }

        // Media URLs
        if (lesson.getVideoURL() != null) {
            etVideoUrl.setText(lesson.getVideoURL());
        }
        if (lesson.getAudioURL() != null) {
            etAudioUrl.setText(lesson.getAudioURL());
        }

        // Status switches
        switchPublicStatus.setChecked(lesson.getTrangThai() != null ? lesson.getTrangThai() : false);
        switchPremiumStatus.setChecked(lesson.getLaBaiGiangGoi() != null ? lesson.getLaBaiGiangGoi() : false);

        // Dropdown selections (if available)
        if (lesson.getCapDoHSK() != null) {
            String hskText = "HSK " + lesson.getCapDoHSK().getCapDo();
            spinnerHSKLevel.setText(hskText, false);
        }
        if (lesson.getChuDe() != null) {
            spinnerTopic.setText(lesson.getChuDe().getTen(), false);
        }
        if (lesson.getLoaiBaiGiang() != null) {
            spinnerLessonType.setText(lesson.getLoaiBaiGiang().getTen(), false);
        }

        // Load thumbnail image
        if (lesson.getHinhAnh() != null && !lesson.getHinhAnh().isEmpty()) {
            loadThumbnailImage(lesson.getHinhAnh());
        }

        Log.d(TAG, "✅ Form populated successfully");
    }

    private void loadThumbnailImage(String imagePath) {
        String imageUrl = Constants.getBaseUrl() + Constants.API_VIEW_IMAGE + imagePath;
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_lesson)
                .error(R.drawable.placeholder_lesson)
                .into(ivThumbnail);

        tvThumbnailInfo.setText("Hình hiện tại: " + imagePath);
    }

    // ===== IMAGE SELECTION =====

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình thumbnail"), REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImagePath = selectedImageUri.toString();
                ivThumbnail.setImageURI(selectedImageUri);
                tvThumbnailInfo.setText("Hình mới đã chọn");
                Log.d(TAG, "✅ Image selected: " + selectedImagePath);
            }
        }
    }

    // ===== FORM VALIDATION & SAVING =====

    private boolean validateForm() {
        boolean isValid = true;

        // Validate title
        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            isValid = false;
        } else if (title.length() < 5) {
            etTitle.setError("Tiêu đề phải có ít nhất 5 ký tự");
            isValid = false;
        } else if (title.length() > 200) {
            etTitle.setError("Tiêu đề không được quá 200 ký tự");
            isValid = false;
        }

        // Validate content
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            etContent.setError("Vui lòng nhập nội dung bài giảng");
            isValid = false;
        } else if (content.length() < 20) {
            etContent.setError("Nội dung phải có ít nhất 20 ký tự");
            isValid = false;
        }

        // Validate duration
        String durationStr = etDuration.getText().toString().trim();
        if (!TextUtils.isEmpty(durationStr)) {
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration < 1 || duration > 300) {
                    etDuration.setError("Thời lượng phải từ 1-300 phút");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etDuration.setError("Thời lượng không hợp lệ");
                isValid = false;
            }
        }

        // Validate URLs if provided
        String videoUrl = etVideoUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(videoUrl) && !isValidUrl(videoUrl)) {
            etVideoUrl.setError("URL video không hợp lệ");
            isValid = false;
        }

        String audioUrl = etAudioUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(audioUrl) && !isValidUrl(audioUrl)) {
            etAudioUrl.setError("URL audio không hợp lệ");
            isValid = false;
        }

        // Validate HSK Level (required)
        if (selectedCapDoHSK == null) {
            spinnerHSKLevel.setError("Vui lòng chọn cấp độ HSK");
            isValid = false;
        }

        // Validate Lesson Type (required)
        if (selectedLoaiBaiGiang == null) {
            spinnerLessonType.setError("Vui lòng chọn loại bài giảng");
            isValid = false;
        }

        // Validate Topic (required)
        if (selectedChuDe == null) {
            spinnerTopic.setError("Vui lòng chọn chủ đề");
            isValid = false;
        }

        Log.d(TAG, "Form validation - HSK: " + (selectedCapDoHSK != null ? selectedCapDoHSK.getTenCapDo() : "null") +
                ", Type: " + (selectedLoaiBaiGiang != null ? selectedLoaiBaiGiang.getTenLoai() : "null") +
                ", Topic: " + (selectedChuDe != null ? selectedChuDe.getTenChuDe() : "null"));

        return isValid;
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void saveLessonData() {
        Log.d(TAG, "💾 Attempting to save lesson data");

        if (!validateForm()) {
            Toast.makeText(this, "Vui lòng kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);
        progressDialog.show();

        if (isEditMode && lessonId != null) {
            updateLesson();
        } else {
            createNewLesson();
        }
    }

    private void createNewLesson() {
        Log.d(TAG, "🆕 Creating new lesson");

        ApiService.TeacherBaiGiangRequest.CreateRequest request = buildCreateRequest();
        controller.createBaiGiang(request);
    }

    private void updateLesson() {
        Log.d(TAG, "✏️ Updating lesson ID: " + lessonId);

        ApiService.TeacherBaiGiangRequest.UpdateRequest request = buildUpdateRequest();
        controller.updateBaiGiang(lessonId, request);
    }

    private ApiService.TeacherBaiGiangRequest.CreateRequest buildCreateRequest() {
        ApiService.TeacherBaiGiangRequest.CreateRequest request =
                new ApiService.TeacherBaiGiangRequest.CreateRequest();

        request.setTieuDe(etTitle.getText().toString().trim());
        request.setMoTa(etDescription.getText().toString().trim());
        request.setNoiDung(etContent.getText().toString().trim());

        // Duration
        String durationStr = etDuration.getText().toString().trim();
        if (!TextUtils.isEmpty(durationStr)) {
            try {
                request.setThoiLuong(Integer.parseInt(durationStr));
            } catch (NumberFormatException e) {
                // Use default duration if parsing fails
                request.setThoiLuong(15);
            }
        }

        // URLs
        String videoUrl = etVideoUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(videoUrl)) {
            request.setVideoURL(videoUrl);
        }

        String audioUrl = etAudioUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(audioUrl)) {
            request.setAudioURL(audioUrl);
        }

        // Status
        request.setTrangThai(switchPublicStatus.isChecked());
        request.setLaBaiGiangGoi(switchPremiumStatus.isChecked());

        // ✅ Required fields - Add category IDs
        if (selectedCapDoHSK != null) {
            request.setCapDoHSKId(selectedCapDoHSK.getID());
        }

        if (selectedLoaiBaiGiang != null) {
            request.setLoaiBaiGiangId(selectedLoaiBaiGiang.getID());
        }

        if (selectedChuDe != null) {
            request.setChuDeId(selectedChuDe.getID());
        }

        Log.d(TAG, "Create request - HSK ID: " + (selectedCapDoHSK != null ? selectedCapDoHSK.getID() : "null") +
                ", Type ID: " + (selectedLoaiBaiGiang != null ? selectedLoaiBaiGiang.getID() : "null") +
                ", Topic ID: " + (selectedChuDe != null ? selectedChuDe.getID() : "null"));

        return request;
    }

    private ApiService.TeacherBaiGiangRequest.UpdateRequest buildUpdateRequest() {
        ApiService.TeacherBaiGiangRequest.UpdateRequest request =
                new ApiService.TeacherBaiGiangRequest.UpdateRequest();

        request.setTieuDe(etTitle.getText().toString().trim());
        request.setMoTa(etDescription.getText().toString().trim());
        request.setNoiDung(etContent.getText().toString().trim());

        // Duration
        String durationStr = etDuration.getText().toString().trim();
        if (!TextUtils.isEmpty(durationStr)) {
            try {
                request.setThoiLuong(Integer.parseInt(durationStr));
            } catch (NumberFormatException e) {
                // Use default duration if parsing fails
                request.setThoiLuong(15);
            }
        }

        // URLs
        String videoUrl = etVideoUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(videoUrl)) {
            request.setVideoURL(videoUrl);
        }

        String audioUrl = etAudioUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(audioUrl)) {
            request.setAudioURL(audioUrl);
        }

        // Status
        request.setTrangThai(switchPublicStatus.isChecked());
        request.setLaBaiGiangGoi(switchPremiumStatus.isChecked());

        // ✅ Required fields - Add category IDs
        if (selectedCapDoHSK != null) {
            request.setCapDoHSKId(selectedCapDoHSK.getID());
        }

        if (selectedLoaiBaiGiang != null) {
            request.setLoaiBaiGiangId(selectedLoaiBaiGiang.getID());
        }

        if (selectedChuDe != null) {
            request.setChuDeId(selectedChuDe.getID());
        }

        Log.d(TAG, "Update request - HSK ID: " + (selectedCapDoHSK != null ? selectedCapDoHSK.getID() : "null") +
                ", Type ID: " + (selectedLoaiBaiGiang != null ? selectedLoaiBaiGiang.getID() : "null") +
                ", Topic ID: " + (selectedChuDe != null ? selectedChuDe.getID() : "null"));

        return request;
    }

    // ===== HELPER METHODS =====

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean hasUnsavedChanges() {
        if (!isEditMode) {
            // For new lesson, check if any field has content
            return !TextUtils.isEmpty(etTitle.getText().toString().trim()) ||
                    !TextUtils.isEmpty(etDescription.getText().toString().trim()) ||
                    !TextUtils.isEmpty(etContent.getText().toString().trim());
        } else if (currentLesson != null) {
            // For edit mode, check if any field has changed
            return !etTitle.getText().toString().trim().equals(currentLesson.getTieuDe() != null ? currentLesson.getTieuDe() : "") ||
                    !etDescription.getText().toString().trim().equals(currentLesson.getMoTa() != null ? currentLesson.getMoTa() : "") ||
                    !etContent.getText().toString().trim().equals(currentLesson.getNoiDung() != null ? currentLesson.getNoiDung() : "");
        }
        return false;
    }

    private void showDiscardChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy thay đổi")
                .setMessage("Bạn có chắc chắn muốn hủy? Các thay đổi chưa lưu sẽ bị mất.")
                .setPositiveButton("Hủy thay đổi", (dialog, which) -> finish())
                .setNegativeButton("Tiếp tục chỉnh sửa", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    // ===== MENU HANDLING =====

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            showDiscardChangesDialog();
        } else {
            super.onBackPressed();
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
        populateFormData(baiGiang);
    }

    @Override
    public void onBaiGiangCreated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Lesson created successfully: " + baiGiang.getTieuDe());
        showProgress(false);
        progressDialog.hide();

        Toast.makeText(this, "Tạo bài giảng thành công!", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBaiGiangUpdated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang) {
        Log.d(TAG, "✅ Lesson updated successfully: " + baiGiang.getTieuDe());
        showProgress(false);
        progressDialog.hide();

        Toast.makeText(this, "Cập nhật bài giảng thành công!", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBaiGiangDeleted() {
        // Not used in this activity
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
        progressDialog.hide();

        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
    }
}