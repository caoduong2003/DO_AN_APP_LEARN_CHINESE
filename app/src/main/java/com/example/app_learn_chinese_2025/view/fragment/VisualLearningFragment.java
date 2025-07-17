package com.example.app_learn_chinese_2025.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.ClaudeResponse;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.data.VisualLearning;
import com.example.app_learn_chinese_2025.model.remote.ClaudeApiManager;
import com.example.app_learn_chinese_2025.model.repository.VisualLearningRepository;
import com.example.app_learn_chinese_2025.util.ImageUtils;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.activity.VisualLearningHistoryActivity;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class VisualLearningFragment extends Fragment {
    private static final String TAG = "VisualLearningFragment";
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
    private Button historyButton;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_visual_learning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        try {
            initializeComponents(view);
            setupClickListeners();
            checkSavedCount();

            if (checkCameraPermission()) {
                startCamera();
            } else {
                requestCameraPermission();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeComponents(View view) {
        Log.d(TAG, "Initializing components");

        // Initialize UI components
        previewView = view.findViewById(R.id.previewView);
        capturedImageView = view.findViewById(R.id.capturedImageView);
        resultObjectText = view.findViewById(R.id.resultObjectText);
        resultVocabularyText = view.findViewById(R.id.resultVocabularyText);
        resultPinyinText = view.findViewById(R.id.resultPinyinText);
        resultVietnameseText = view.findViewById(R.id.resultVietnameseText);
        resultExampleText = view.findViewById(R.id.resultExampleText);
        captureButton = view.findViewById(R.id.captureButton);
        retakeButton = view.findViewById(R.id.retakeButton);
        saveButton = view.findViewById(R.id.saveButton);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        resultContainer = view.findViewById(R.id.resultContainer);
        historyButton = view.findViewById(R.id.historyButton);

        // Check if all required views were found
        if (previewView == null || capturedImageView == null || captureButton == null) {
            throw new RuntimeException("Required views not found in layout");
        }

        // Initialize data components
        repository = new VisualLearningRepository(requireActivity().getApplication());
        claudeApiManager = new ClaudeApiManager();
        sessionManager = new SessionManager(requireContext());

        // Initial state
        showCameraMode();

        Log.d(TAG, "Components initialized successfully");
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners");

        if (captureButton != null) {
            captureButton.setOnClickListener(v -> captureImage());
        }

        if (retakeButton != null) {
            retakeButton.setOnClickListener(v -> showCameraMode());
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveToDatabase());
        }

        if (historyButton != null) {
            historyButton.setOnClickListener(v -> openHistoryActivity());
        }

        Log.d(TAG, "Click listeners setup successfully");
    }

    private void openHistoryActivity() {
        Log.d(TAG, "Opening history activity");

        try {
            Intent intent = new Intent(requireContext(), VisualLearningHistoryActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening history: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Lỗi mở lịch sử: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //Kiểm tra và hiển thị số lượng từ vựng đã lưu
    private void checkSavedCount() {
        if (sessionManager == null) return;

        User currentUser = sessionManager.getUserDetails();
        if (currentUser != null) {
            long hocVienId = currentUser.getID();

            // Observe count
            repository.getCount(hocVienId).observe(getViewLifecycleOwner(), count -> {
                if (count != null && historyButton != null) {
                    String buttonText = count > 0 ?
                            "Lịch sử (" + count + ")" :
                            "Lịch sử";
                    historyButton.setText(buttonText);

                    // Enable/disable button based on count
                    historyButton.setEnabled(count > 0);

                    Log.d(TAG, "Updated history button: " + count + " items saved");
                }
            });
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        Log.d(TAG, "Requesting camera permission");
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted");
                startCamera();
            } else {
                Log.w(TAG, "Camera permission denied");
                Toast.makeText(requireContext(), "Cần quyền camera để sử dụng chức năng này", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCamera() {
        Log.d(TAG, "Starting camera");

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage(), e);
                Toast.makeText(requireContext(), "Lỗi khởi động camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Log.d(TAG, "Binding camera use cases");

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

            Log.d(TAG, "Camera use cases bound successfully");

        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Lỗi kết nối camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage() {
        Log.d(TAG, "Capturing image");

        if (imageCapture == null) {
            Log.w(TAG, "ImageCapture is null");
            return;
        }

        // Create file for the image
        String fileName = "VL_" + System.currentTimeMillis() + ".jpg";
        File photoFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Show loading
        captureButton.setEnabled(false);
        loadingProgress.setVisibility(View.VISIBLE);

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        currentImagePath = photoFile.getAbsolutePath();
                        Log.d(TAG, "Image saved: " + currentImagePath);

                        showCapturedImage();
                        analyzeImageWithClaude();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Image capture failed: " + exception.getMessage(), exception);
                        Toast.makeText(requireContext(), "Lỗi chụp ảnh: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        captureButton.setEnabled(true);
                        loadingProgress.setVisibility(View.GONE);
                    }
                });
    }

    private void showCapturedImage() {
        requireActivity().runOnUiThread(() -> {
            Log.d(TAG, "Showing captured image");

            // Hide camera preview
            previewView.setVisibility(View.GONE);
            captureButton.setVisibility(View.GONE);

            // Show captured image
            capturedImageView.setVisibility(View.VISIBLE);
            retakeButton.setVisibility(View.VISIBLE);

            // Load image using Glide
            if (currentImagePath != null) {
                Glide.with(this)
                        .load(currentImagePath)
                        .into(capturedImageView);
            }
        });
    }

    private void analyzeImageWithClaude() {
        Log.d(TAG, "Starting image analysis with Claude");

        // Encode image to base64
        String base64Image = ImageUtils.encodeImageToBase64(currentImagePath);
        if (base64Image == null) {
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Lỗi xử lý hình ảnh", Toast.LENGTH_SHORT).show();
                loadingProgress.setVisibility(View.GONE);
                captureButton.setEnabled(true);
            });
            return;
        }

        // Call Claude API
        claudeApiManager.analyzeImage(base64Image, new ClaudeApiManager.OnAnalyzeImageListener() {
            @Override
            public void onSuccess(ClaudeResponse response) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "Claude analysis successful");
                    currentResponse = response;
                    displayResults(response);
                    loadingProgress.setVisibility(View.GONE);
                    captureButton.setEnabled(true);
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Claude analysis failed: " + errorMessage);
                    Toast.makeText(requireContext(), "Lỗi phân tích AI: " + errorMessage, Toast.LENGTH_LONG).show();
                    loadingProgress.setVisibility(View.GONE);
                    captureButton.setEnabled(true);
                });
            }
        });
    }

    private void displayResults(ClaudeResponse response) {
        Log.d(TAG, "Displaying results");

        resultContainer.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        // Set text content
        resultObjectText.setText("Đồ vật: " + response.getObject());
        resultVocabularyText.setText("中文: " + response.getVocabulary());
        resultPinyinText.setText("Pinyin: " + response.getPinyin());
        resultVietnameseText.setText("Tiếng Việt: " + response.getVietnamese());
        resultExampleText.setText("Ví dụ: " + response.getExample());

        // Enable save button
        saveButton.setEnabled(true);
    }

    private void saveToDatabase() {
        Log.d(TAG, "Saving to database");

        // Validation dữ liệu
        if (currentResponse == null || currentImagePath == null) {
            Toast.makeText(requireContext(), "Không có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin user
        long hocVienId = 0;
        User currentUser = sessionManager.getUserDetails();
        if (currentUser != null) {
            hocVienId = currentUser.getID();
        } else {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để lưu từ vựng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hocVienId <= 0) {
            Toast.makeText(requireContext(), "Lỗi thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng VisualLearning
        VisualLearning visualLearning = new VisualLearning(
                hocVienId,
                currentImagePath,
                currentResponse.getObject(),
                currentResponse.getVocabulary(),
                currentResponse.getPinyin(),
                currentResponse.getVietnamese(),
                currentResponse.getExample()
        );

        // UI feedback
        saveButton.setEnabled(false);
        saveButton.setText("Đang lưu...");

        // Thực hiện insert vào database
        repository.insert(visualLearning, new VisualLearningRepository.OnInsertCompleteListener() {
            @Override
            public void onInsertComplete(long id) {
                requireActivity().runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Lưu");

                    if (id > 0) {
                        Log.d(TAG, "Save successful, ID: " + id);
                        Toast.makeText(requireContext(), "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Save failed");
                        Toast.makeText(requireContext(), "Lưu thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showCameraMode() {
        Log.d(TAG, "Showing camera mode");

        // Show camera components
        previewView.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);

        // Hide result components
        capturedImageView.setVisibility(View.GONE);
        retakeButton.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);

        // Reset data
        currentImagePath = null;
        currentResponse = null;

        // Reset button states
        captureButton.setEnabled(true);
        saveButton.setEnabled(true);
        saveButton.setText("Lưu");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment destroyed");

        // Clean up camera resources
        if (camera != null) {
            camera = null;
        }
    }
    public void refreshData() {
        Log.d(TAG, "refreshData() called");

        if (getActivity() == null || !isAdded()) {
            Log.w(TAG, "Fragment not attached, cannot refresh");
            return;
        }

        getActivity().runOnUiThread(() -> {
            try {
                Log.d(TAG, "Executing refresh on UI thread");

                // Reset về camera mode
                showCameraMode();

                // Restart camera nếu có permission
                if (checkCameraPermission()) {
                    startCamera();
                    Log.d(TAG, "Camera restarted during refresh");
                } else {
                    Log.w(TAG, "No camera permission during refresh");
                    requestCameraPermission();
                }

                Log.d(TAG, "Visual Learning Fragment refreshed successfully");

            } catch (Exception e) {
                Log.e(TAG, "Error refreshing fragment: " + e.getMessage(), e);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi làm mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method để clear cache và reset hoàn toàn
     */
    public void clearCache() {
        Log.d(TAG, "clearCache() called");

        if (getActivity() == null || !isAdded()) {
            Log.w(TAG, "Fragment not attached, cannot clear cache");
            return;
        }

        getActivity().runOnUiThread(() -> {
            try {
                Log.d(TAG, "Clearing cache on UI thread");

                // Clear current data
                currentImagePath = null;
                currentResponse = null;

                // Reset UI
                showCameraMode();

                // Clear captured image
                if (capturedImageView != null) {
                    capturedImageView.setImageDrawable(null);
                }

                // Reset buttons
                resetButtonStates();

                // Hide loading
                if (loadingProgress != null) {
                    loadingProgress.setVisibility(View.GONE);
                }

                // Restart camera
                if (checkCameraPermission()) {
                    startCamera();
                }

                Log.d(TAG, "Cache cleared successfully");

            } catch (Exception e) {
                Log.e(TAG, "Error clearing cache: " + e.getMessage(), e);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi xóa cache: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Reset button states
     */
    private void resetButtonStates() {
        if (captureButton != null) {
            captureButton.setEnabled(true);
            captureButton.setText("Chụp ảnh");
        }

        if (saveButton != null) {
            saveButton.setEnabled(true);
            saveButton.setText("Lưu");
        }

        if (retakeButton != null) {
            retakeButton.setEnabled(true);
        }
    }

    /**
     * Method để check fragment state
     */
    public boolean isFragmentReady() {
        boolean ready = previewView != null &&
                captureButton != null &&
                isAdded() &&
                !isDetached() &&
                getView() != null &&
                getActivity() != null;

        Log.d(TAG, "Fragment ready state: " + ready);
        return ready;
    }

    /**
     * Force restart camera
     */
    public void forceRestartCamera() {
        Log.d(TAG, "forceRestartCamera() called");

        if (!isFragmentReady()) {
            Log.w(TAG, "Fragment not ready for camera restart");
            return;
        }

        getActivity().runOnUiThread(() -> {
            try {
                // Stop current camera first
                if (camera != null) {
                    camera = null;
                }

                // Restart after short delay
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (isFragmentReady() && checkCameraPermission()) {
                        startCamera();
                        Log.d(TAG, "Camera force restarted");
                    }
                }, 100);

            } catch (Exception e) {
                Log.e(TAG, "Error force restarting camera: " + e.getMessage(), e);
            }
        });
    }
}