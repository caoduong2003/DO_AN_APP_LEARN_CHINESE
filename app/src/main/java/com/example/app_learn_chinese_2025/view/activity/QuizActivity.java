package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiTapController;
import com.example.app_learn_chinese_2025.model.data.BaiTap;
import com.example.app_learn_chinese_2025.model.data.CauHoi;
import com.example.app_learn_chinese_2025.model.data.DapAn;
import com.example.app_learn_chinese_2025.model.data.KetQuaBaiTap;
import com.example.app_learn_chinese_2025.model.request.LamBaiTapRequest;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity implements BaiTapController.BaiTapControllerListener {

    private static final String TAG = "QUIZ_ACTIVITY";

    // UI Components
    private Toolbar toolbar;
    private TextView tvQuizTitle;
    private TextView tvQuestionNumber;
    private TextView tvTimeRemaining;
    private TextView tvQuestionContent;
    private ImageView ivQuestionImage;
    private Button btnPlayAudio;
    private RadioGroup rgAnswers;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private LinearLayout layoutContent;
    private LinearLayout layoutLoading;
    private TextView tvLoadingMessage;

    // Data
    private Long baiTapId;
    private String baiTapTitle;
    private BaiTap baiTap;
    private List<CauHoi> cauHoiList;
    private List<Long> userAnswers; // Store selected answer IDs
    private int currentQuestionIndex = 0;
    private long startTime; // Thời gian bắt đầu làm bài

    // Controllers
    private BaiTapController baiTapController;

    // Timer
    private CountDownTimer quizTimer;
    private long timeRemainingInMillis;

    // Audio player
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get intent data
        Intent intent = getIntent();
        baiTapId = intent.getLongExtra("BAI_TAP_ID", -1);
        baiTapTitle = intent.getStringExtra("BAI_TAP_TITLE");

        if (baiTapId == -1) {
            Log.e(TAG, "Invalid bai tap ID");
            Toast.makeText(this, "Lỗi: Không tìm thấy bài tập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lưu thời gian bắt đầu
        startTime = System.currentTimeMillis();

        Log.d(TAG, "Starting quiz with ID: " + baiTapId + ", Title: " + baiTapTitle);

        // Initialize
        initViews();
        setupToolbar();
        initControllers();

        // Load quiz data
        loadQuizData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        rgAnswers = findViewById(R.id.rgAnswers);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        layoutContent = findViewById(R.id.layoutContent);
        layoutLoading = findViewById(R.id.layoutLoading);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);

        // Setup click listeners
        btnPrevious.setOnClickListener(v -> previousQuestion());
        btnNext.setOnClickListener(v -> nextQuestion());
        btnSubmit.setOnClickListener(v -> showSubmitDialog());
        btnPlayAudio.setOnClickListener(v -> playAudio());

        // Initially hide content and show loading
        layoutContent.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);

        if (tvLoadingMessage != null) {
            tvLoadingMessage.setText("Đang tải bài tập...");
        }

        Log.d(TAG, "Views initialized");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bài tập");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initControllers() {
        SessionManager sessionManager = new SessionManager(this);
        baiTapController = new BaiTapController(sessionManager);
        baiTapController.setListener(this);

        Log.d(TAG, "Controllers initialized");
    }

    private void loadQuizData() {
        Log.d(TAG, "Loading quiz data for ID: " + baiTapId);
        baiTapController.getBaiTapDetail(baiTapId);
    }

    private void setupQuiz() {
        if (baiTap == null) {
            Log.e(TAG, "BaiTap is null");
            showError("Không thể tải thông tin bài tập");
            return;
        }

        if (baiTap.getCauHoiList() == null || baiTap.getCauHoiList().isEmpty()) {
            Log.e(TAG, "No questions found");
            showError("Bài tập này chưa có câu hỏi");
            return;
        }

        // Set quiz title
        tvQuizTitle.setText(baiTap.getTieuDe() != null ? baiTap.getTieuDe() : "Bài tập");

        // Initialize data
        cauHoiList = baiTap.getCauHoiList();
        userAnswers = new ArrayList<>();

        // Initialize user answers with null values
        for (int i = 0; i < cauHoiList.size(); i++) {
            userAnswers.add(null);
        }

        // Setup timer
        setupTimer();

        // Show first question
        currentQuestionIndex = 0;
        displayQuestion();

        // Show content
        layoutLoading.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);

        Log.d(TAG, "Quiz setup completed with " + cauHoiList.size() + " questions");
    }

    private void setupTimer() {
        if (baiTap.getThoiGianLam() != null && baiTap.getThoiGianLam() > 0) {
            timeRemainingInMillis = baiTap.getThoiGianLam() * 60 * 1000L; // Convert minutes to milliseconds

            quizTimer = new CountDownTimer(timeRemainingInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeRemainingInMillis = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Timer finished - auto submit");
                    autoSubmitQuiz();
                }
            };

            quizTimer.start();
            Log.d(TAG, "Timer started for " + baiTap.getThoiGianLam() + " minutes");
        } else {
            // No time limit
            tvTimeRemaining.setText("Không giới hạn");
            Log.d(TAG, "No time limit for this quiz");
        }
    }

    private void updateTimerDisplay() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemainingInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemainingInMillis) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTimeRemaining.setText(timeFormatted);

        // Change color when time is running low
        if (timeRemainingInMillis < 60000) { // Less than 1 minute
            tvTimeRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= cauHoiList.size()) {
            Log.e(TAG, "Invalid question index: " + currentQuestionIndex);
            return;
        }

        CauHoi cauHoi = cauHoiList.get(currentQuestionIndex);

        // Update question number
        tvQuestionNumber.setText("Câu " + (currentQuestionIndex + 1) + "/" + cauHoiList.size());

        // Update progress
        int progress = (int) (((float) (currentQuestionIndex + 1) / cauHoiList.size()) * 100);
        progressBar.setProgress(progress);

        // Display question content
        tvQuestionContent.setText(cauHoi.getNoiDung() != null ? cauHoi.getNoiDung() : "");

        // Handle question image
        if (cauHoi.hasImage()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(cauHoi.getHinhAnh())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        // Handle audio
        if (cauHoi.hasAudio()) {
            btnPlayAudio.setVisibility(View.VISIBLE);
        } else {
            btnPlayAudio.setVisibility(View.GONE);
        }

        // Setup answers
        setupAnswers(cauHoi);

        // Update navigation buttons
        updateNavigationButtons();

        Log.d(TAG, "Displayed question " + (currentQuestionIndex + 1) + ": " + cauHoi.getNoiDung());
    }

    private void setupAnswers(CauHoi cauHoi) {
        // Clear previous answers
        rgAnswers.removeAllViews();

        List<DapAn> dapAnList = cauHoi.getDapAnList();
        if (dapAnList == null || dapAnList.isEmpty()) {
            Log.e(TAG, "No answers found for question");
            TextView noAnswersText = new TextView(this);
            noAnswersText.setText("Không có đáp án cho câu hỏi này");
            noAnswersText.setPadding(16, 16, 16, 16);
            rgAnswers.addView(noAnswersText);
            return;
        }

        // Add radio buttons for each answer
        for (int i = 0; i < dapAnList.size(); i++) {
            DapAn dapAn = dapAnList.get(i);

            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(dapAn.getNoiDung() != null ? dapAn.getNoiDung() : "");
            radioButton.setId(View.generateViewId());
            radioButton.setTag(dapAn.getId());

            // Set padding and styling
            radioButton.setPadding(16, 16, 16, 16);
            radioButton.setTextSize(16);

            rgAnswers.addView(radioButton);
        }

        // Restore user's previous answer if exists
        Long previousAnswer = userAnswers.get(currentQuestionIndex);
        if (previousAnswer != null) {
            for (int i = 0; i < rgAnswers.getChildCount(); i++) {
                View child = rgAnswers.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getTag() != null && rb.getTag().equals(previousAnswer)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        }

        // Set selection listener
        rgAnswers.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                if (selectedRadioButton != null && selectedRadioButton.getTag() != null) {
                    Long answerId = (Long) selectedRadioButton.getTag();
                    userAnswers.set(currentQuestionIndex, answerId);
                    Log.d(TAG, "Answer selected for question " + (currentQuestionIndex + 1) + ": " + answerId);
                }
            }
        });
    }

    private void updateNavigationButtons() {
        // Previous button
        btnPrevious.setEnabled(currentQuestionIndex > 0);
        btnPrevious.setVisibility(currentQuestionIndex > 0 ? View.VISIBLE : View.INVISIBLE);

        // Next button
        btnNext.setEnabled(currentQuestionIndex < cauHoiList.size() - 1);
        btnNext.setVisibility(currentQuestionIndex < cauHoiList.size() - 1 ? View.VISIBLE : View.INVISIBLE);

        // Submit button
        btnSubmit.setVisibility(currentQuestionIndex == cauHoiList.size() - 1 ? View.VISIBLE : View.INVISIBLE);
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
            Log.d(TAG, "Moved to previous question: " + (currentQuestionIndex + 1));
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < cauHoiList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
            Log.d(TAG, "Moved to next question: " + (currentQuestionIndex + 1));
        }
    }

    private void playAudio() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= cauHoiList.size()) {
            return;
        }

        CauHoi cauHoi = cauHoiList.get(currentQuestionIndex);

        if (!cauHoi.hasAudio()) {
            Toast.makeText(this, "Không có âm thanh cho câu hỏi này", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(cauHoi.getAudioURL());
            mediaPlayer.prepareAsync();

            btnPlayAudio.setEnabled(false);
            btnPlayAudio.setText("Đang tải...");

            mediaPlayer.setOnPreparedListener(mp -> {
                btnPlayAudio.setText("Đang phát...");
                mp.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayAudio.setEnabled(true);
                btnPlayAudio.setText("▶ Phát lại");
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Media player error: " + what + ", " + extra);
                Toast.makeText(this, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
                btnPlayAudio.setEnabled(true);
                btnPlayAudio.setText("▶ Phát âm thanh");
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error playing audio", e);
            Toast.makeText(this, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
            btnPlayAudio.setEnabled(true);
            btnPlayAudio.setText("▶ Phát âm thanh");
        }
    }

    private void showSubmitDialog() {
        // Count answered questions
        int answeredCount = 0;
        for (Long answer : userAnswers) {
            if (answer != null) {
                answeredCount++;
            }
        }

        String message = "Bạn đã trả lời " + answeredCount + "/" + cauHoiList.size() + " câu hỏi.";

        if (answeredCount < cauHoiList.size()) {
            message += "\n\nVẫn còn " + (cauHoiList.size() - answeredCount) + " câu chưa trả lời.";
            message += "\nBạn có chắc chắn muốn nộp bài không?";
        } else {
            message += "\n\nBạn có muốn nộp bài không?";
        }

        new AlertDialog.Builder(this)
                .setTitle("Nộp bài")
                .setMessage(message)
                .setPositiveButton("Nộp bài", (dialog, which) -> submitQuiz())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void submitQuiz() {
        if (quizTimer != null) {
            quizTimer.cancel();
        }

        // Tính thời gian làm bài (giây)
        int thoiGianLam = (int) ((System.currentTimeMillis() - startTime) / 1000);

        // Tạo danh sách câu trả lời theo format mới
        List<LamBaiTapRequest.TraLoiRequest> danhSachTraLoi = new ArrayList<>();

        for (int i = 0; i < cauHoiList.size(); i++) {
            CauHoi cauHoi = cauHoiList.get(i);
            Long dapAnId = userAnswers.get(i);

            // Tạo TraLoiRequest cho mỗi câu hỏi
            LamBaiTapRequest.TraLoiRequest traLoi = new LamBaiTapRequest.TraLoiRequest();
            traLoi.setCauHoiId(cauHoi.getId());
            traLoi.setDapAnId(dapAnId); // Có thể null nếu không trả lời

            danhSachTraLoi.add(traLoi);
        }

        // Prepare submission data
        LamBaiTapRequest request = new LamBaiTapRequest();
        request.setBaiTapId(baiTapId);
        request.setThoiGianLam(thoiGianLam);
        request.setDanhSachTraLoi(danhSachTraLoi);

        // Show loading
        layoutContent.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        if (tvLoadingMessage != null) {
            tvLoadingMessage.setText("Đang nộp bài...");
        }

        // Submit to server
        baiTapController.submitBaiTap(request);

        Log.d(TAG, "Quiz submitted with " + danhSachTraLoi.size() + " answers, time: " + thoiGianLam + " seconds");
    }

    private void autoSubmitQuiz() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Hết thời gian! Tự động nộp bài.", Toast.LENGTH_LONG).show();
            submitQuiz();
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (layoutContent.getVisibility() == View.VISIBLE) {
            // Quiz is in progress
            new AlertDialog.Builder(this)
                    .setTitle("Thoát bài tập")
                    .setMessage("Bạn có chắc chắn muốn thoát?\n\nKết quả sẽ không được lưu.")
                    .setPositiveButton("Thoát", (dialog, which) -> {
                        if (quizTimer != null) {
                            quizTimer.cancel();
                        }
                        super.onBackPressed();
                    })
                    .setNegativeButton("Tiếp tục", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (quizTimer != null) {
            quizTimer.cancel();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Log.d(TAG, "QuizActivity destroyed");
    }

    // BaiTapController.BaiTapControllerListener implementation
    @Override
    public void onBaiTapListLoaded(List<BaiTap> baiTapList) {
        // Not used in this activity
    }

    @Override
    public void onBaiTapDetailLoaded(BaiTap baiTap) {
        Log.d(TAG, "Quiz data loaded: " + (baiTap != null ? baiTap.getTieuDe() : "null"));
        this.baiTap = baiTap;
        runOnUiThread(this::setupQuiz);
    }

    @Override
    public void onBaiTapSubmitted(KetQuaBaiTap ketQua) {
        Log.d(TAG, "Quiz submitted successfully");
        runOnUiThread(() -> {
            // Show result
            String resultMessage = "Kết quả: " + ketQua.getDiemSo() + "/" +
                    (baiTap != null ? baiTap.getDiemToiDa() : "10") + " điểm";

            if (ketQua.getSoCauDung() != null && ketQua.getTongSoCau() != null) {
                resultMessage += "\nĐúng: " + ketQua.getSoCauDung() + "/" + ketQua.getTongSoCau() + " câu";
            }

            new AlertDialog.Builder(this)
                    .setTitle("Hoàn thành bài tập")
                    .setMessage(resultMessage)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Return result to calling activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SCORE", ketQua.getDiemSo());
                        resultIntent.putExtra("TOTAL_QUESTIONS", cauHoiList != null ? cauHoiList.size() : 0);
                        resultIntent.putExtra("CORRECT_ANSWERS", ketQua.getSoCauDung());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    @Override
    public void onKetQuaListLoaded(List<KetQuaBaiTap> ketQuaList) {
        // Not used in this activity
    }

    @Override
    public void onPingSuccess(String message) {
        // Not used in this activity
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error: " + error);
        runOnUiThread(() -> {
            showError("Lỗi: " + error);

            // Show content if it was hidden
            if (layoutContent.getVisibility() == View.GONE) {
                layoutLoading.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            }
        });
    }
}