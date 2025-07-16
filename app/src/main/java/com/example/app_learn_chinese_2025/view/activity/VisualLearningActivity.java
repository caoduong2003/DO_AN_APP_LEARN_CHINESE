package com.example.app_learn_chinese_2025.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.ClaudeResponse;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.data.VisualLearning;
import com.example.app_learn_chinese_2025.model.remote.ClaudeApiManager;
import com.example.app_learn_chinese_2025.model.repository.VisualLearningRepository;
import com.example.app_learn_chinese_2025.util.ImageUtils;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class VisualLearningActivity extends AppCompatActivity {
    private static final String TAG = "VisualLearningActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    // UI Components
    private PreviewView previewView;
    private ImageView capturedImageView;
    private TextView resultObjectText;
    private TextView resultVocabularyText;
    private TextView resultPinyinText;
    private TextView resultVietnameseText;
    private TextView resultExampleText;
    private Button captureButton;
    private Button retakeButton;
    private Button saveButton;
    private ProgressBar loadingProgress;
    private View resultContainer;
    private ImageButton favoriteButton;

    // Camera
    private Camera camera;
    private ImageCapture imageCapture;
    private Preview preview;

    // Data
    private VisualLearningRepository repository;
    private ClaudeApiManager claudeApiManager;
    private SessionManager sessionManager;
    private String currentImagePath;
    private ClaudeResponse currentResponse;

    public long hocVienId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_learning);

        initializeComponents();
        setupClickListeners();

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeComponents() {
        // Initialize UI components
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        resultObjectText = findViewById(R.id.resultObjectText);
        resultVocabularyText = findViewById(R.id.resultVocabularyText);
        resultPinyinText = findViewById(R.id.resultPinyinText);
        resultVietnameseText = findViewById(R.id.resultVietnameseText);
        resultExampleText = findViewById(R.id.resultExampleText);
        captureButton = findViewById(R.id.captureButton);
        retakeButton = findViewById(R.id.retakeButton);
        saveButton = findViewById(R.id.saveButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        resultContainer = findViewById(R.id.resultContainer);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Initialize data components
        repository = new VisualLearningRepository(getApplication());
        claudeApiManager = new ClaudeApiManager();
        sessionManager = new SessionManager(this);

        // Initial state
        showCameraMode();
    }

    private void setupClickListeners() {
        captureButton.setOnClickListener(v -> captureImage());
        retakeButton.setOnClickListener(v -> showCameraMode());
        saveButton.setOnClickListener(v -> saveToDatabase());
        favoriteButton.setOnClickListener(v -> toggleFavorite());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
//        findViewById(R.id.btnHistory).setOnClickListener(v -> openHistory());
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Cần quyền camera để sử dụng chức năng này", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Lỗi khởi động camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        // Preview
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Camera selector
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll();

            // Bind use cases to camera
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
            Toast.makeText(this, "Lỗi kết nối camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        // Create file for the image
        String fileName = "VL_" + System.currentTimeMillis() + ".jpg";
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Show loading
        captureButton.setEnabled(false);
        loadingProgress.setVisibility(View.VISIBLE);

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        currentImagePath = photoFile.getAbsolutePath();
                        Log.d(TAG, "Image saved: " + currentImagePath);

                        // Show captured image
                        showCapturedImage();

                        // Analyze with Claude AI
                        analyzeImageWithClaude();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Image capture failed", exception);
                        Toast.makeText(VisualLearningActivity.this, "Lỗi chụp ảnh: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        captureButton.setEnabled(true);
                        loadingProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void showCapturedImage() {
        runOnUiThread(() -> {
            // Hide camera preview
            previewView.setVisibility(View.GONE);
            captureButton.setVisibility(View.GONE);

            // Show captured image
            capturedImageView.setVisibility(View.VISIBLE);
            retakeButton.setVisibility(View.VISIBLE);

            // Load image using Glide
            Glide.with(this)
                    .load(currentImagePath)
                    .into(capturedImageView);
        });
    }

    private void analyzeImageWithClaude() {
        Log.d(TAG, "🔍 Starting image analysis with Claude...");


        // Encode image to base64
        String base64Image = ImageUtils.encodeImageToBase64(currentImagePath);
        if (base64Image == null) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Lỗi xử lý hình ảnh", Toast.LENGTH_SHORT).show();
                loadingProgress.setVisibility(View.GONE);
            });
            return;
        }

        // Call Claude API
        claudeApiManager.analyzeImage(base64Image, new ClaudeApiManager.OnAnalyzeImageListener() {
            @Override
            public void onSuccess(ClaudeResponse response) {
                runOnUiThread(() -> {
                    currentResponse = response;
                    displayResults(response);
                    loadingProgress.setVisibility(View.GONE);
                    captureButton.setEnabled(true);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(VisualLearningActivity.this, "Lỗi phân tích AI: " + errorMessage, Toast.LENGTH_LONG).show();
                    loadingProgress.setVisibility(View.GONE);
                    captureButton.setEnabled(true);

                    // Show retry option
                    showRetryOption();
                });
            }
        });
    }

    private void displayResults(ClaudeResponse response) {
        resultContainer.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        favoriteButton.setVisibility(View.VISIBLE);

        // Set text content
        resultObjectText.setText("Đồ vật: " + response.getObject());
        resultVocabularyText.setText("中文: " + response.getVocabulary());
        resultPinyinText.setText("Pinyin: " + response.getPinyin());
        resultVietnameseText.setText("Tiếng Việt: " + response.getVietnamese());
        resultExampleText.setText("Ví dụ: " + response.getExample());

        // Enable save button
        saveButton.setEnabled(true);
    }

    private void showRetryOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lỗi phân tích")
                .setMessage("Không thể phân tích hình ảnh. Bạn có muốn thử lại không?")
                .setPositiveButton("Thử lại", (dialog, which) -> analyzeImageWithClaude())
                .setNegativeButton("Chụp lại", (dialog, which) -> showCameraMode())
                .setNeutralButton("Hủy", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void saveToDatabase() {
        if (currentResponse == null || currentImagePath == null) {
            Toast.makeText(this, "Không có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        User currentUser = sessionManager.getUserDetails();
        if (currentUser != null) {
            long hocVienId = currentUser.getID();
        }

        VisualLearning visualLearning = new VisualLearning(
                hocVienId,
                currentImagePath,
                currentResponse.getObject(),
                currentResponse.getVocabulary(),
                currentResponse.getPinyin(),
                currentResponse.getVietnamese(),
                currentResponse.getExample()
        );

        saveButton.setEnabled(false);
        saveButton.setText("Đang lưu...");

        repository.insert(visualLearning, new VisualLearningRepository.OnInsertCompleteListener() {
            @Override
            public void onInsertComplete(long id) {
                runOnUiThread(() -> {
                    Toast.makeText(VisualLearningActivity.this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                    saveButton.setText("Đã lưu");
                    saveButton.setEnabled(false);

                    // Show success animation or effect
                    showSaveSuccessAnimation();
                });
            }
        });
    }

    private void showSaveSuccessAnimation() {
        // Simple animation for success feedback
        saveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.success)));

        // Reset after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            saveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
        }, 2000);
    }

    private void toggleFavorite() {
        // This would be implemented when we have the item ID after saving
        // For now, just show a toast
        Toast.makeText(this, "Vui lòng lưu trước khi đánh dấu yêu thích", Toast.LENGTH_SHORT).show();
    }

    private void showCameraMode() {
        // Show camera components
        previewView.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);

        // Hide result components
        capturedImageView.setVisibility(View.GONE);
        retakeButton.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        favoriteButton.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);

        // Reset data
        currentImagePath = null;
        currentResponse = null;

        // Reset button states
        captureButton.setEnabled(true);
        saveButton.setEnabled(true);
        saveButton.setText("Lưu vào bộ sưu tập");
    }

//    private void openHistory() {
//        Intent intent = new Intent(this, VisualLearningHistoryActivity.class);
//        startActivity(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up camera resources
        if (camera != null) {
            camera = null;
        }
    }
}