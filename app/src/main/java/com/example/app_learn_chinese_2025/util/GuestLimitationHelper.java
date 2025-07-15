package com.example.app_learn_chinese_2025.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.view.activity.LoginActivity;
import com.example.app_learn_chinese_2025.view.activity.RegisterActivity;

/**
 * 🚀 Helper class quản lý giới hạn và prompts cho Guest Mode
 */
public class GuestLimitationHelper {
    private static final String TAG = "GuestLimitationHelper";

    private Context context;
    private GuestUsageTracker usageTracker;

    public GuestLimitationHelper(Context context) {
        this.context = context;
        this.usageTracker = new GuestUsageTracker(context);
    }

    /**
     * 🎯 Kiểm tra và hiển thị limitation cho bài giảng
     */
    public boolean checkAndShowLessonLimitation(Activity activity) {
        if (!usageTracker.canAccessLesson()) {
            showLessonLimitationDialog(activity);
            return false;
        }
        return true;
    }

    /**
     * 🎯 Kiểm tra và hiển thị limitation cho từ vựng
     */
    public boolean checkAndShowVocabularyLimitation(Activity activity, long baiGiangId) {
        if (!usageTracker.canAccessVocabulary(baiGiangId)) {
            showVocabularyLimitationDialog(activity);
            return false;
        }
        return true;
    }

    /**
     * 🎯 Kiểm tra và hiển thị limitation cho dịch thuật
     */
    public boolean checkAndShowTranslationLimitation(Activity activity) {
        if (!usageTracker.canTranslate()) {
            showTranslationLimitationDialog(activity);
            return false;
        }
        return true;
    }

    /**
     * 🎯 Ghi nhận usage sau khi access thành công
     */
    public void recordLessonAccess() {
        usageTracker.recordLessonAccess();
    }

    public void recordVocabularyAccess(long baiGiangId) {
        usageTracker.recordVocabularyAccess(baiGiangId);
    }

    public void recordTranslationUsage() {
        usageTracker.recordTranslationUsage();
    }

    /**
     * 🎯 Hiển thị dialog limitation cho bài giảng
     */
    private void showLessonLimitationDialog(Activity activity) {
        Log.d(TAG, "Showing lesson limitation dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Giới hạn trải nghiệm")
                .setMessage("Bạn đã xem đủ 3 bài giảng trong ngày!\n\n" +
                        "Đăng ký để trải nghiệm không giới hạn:\n" +
                        "• Xem tất cả bài giảng\n" +
                        "• Làm bài tập và kiểm tra\n" +
                        "• Theo dõi tiến trình học tập\n" +
                        "• Lưu từ vựng yêu thích")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    Log.d(TAG, "User chose to register from lesson limitation");
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .setNegativeButton("Đăng nhập", (dialog, which) -> {
                    Log.d(TAG, "User chose to login from lesson limitation");
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                })
                .setNeutralButton("Quay lại", (dialog, which) -> {
                    Log.d(TAG, "User dismissed lesson limitation dialog");
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 🎯 Hiển thị dialog limitation cho từ vựng
     */
    private void showVocabularyLimitationDialog(Activity activity) {
        Log.d(TAG, "Showing vocabulary limitation dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Giới hạn từ vựng")
                .setMessage("Bạn đã xem đủ 5 từ vựng cho bài giảng này!\n\n" +
                        "Đăng ký để xem toàn bộ từ vựng và nhiều tính năng khác:")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    /**
     * 🎯 Hiển thị dialog limitation cho dịch thuật
     */
    private void showTranslationLimitationDialog(Activity activity) {
        Log.d(TAG, "Showing translation limitation dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Giới hạn dịch thuật")
                .setMessage("Bạn đã sử dụng đủ 10 lần dịch thuật trong ngày!\n\n" +
                        "Đăng ký để dịch thuật không giới hạn:")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    /**
     * 🎯 Hiển thị upgrade prompt thông minh
     */
    public void showSmartUpgradePrompt(Activity activity) {
        GuestUsageTracker.UsageStats stats = usageTracker.getTodayStats();

        if (stats.isNearingLimit()) {
            showNearingLimitPrompt(activity, stats);
        } else if (stats.totalSessions >= 3) {
            showEngagementPrompt(activity, stats);
        }
    }

    /**
     * 🎯 Hiển thị prompt khi gần đạt giới hạn
     */
    private void showNearingLimitPrompt(Activity activity, GuestUsageTracker.UsageStats stats) {
        Log.d(TAG, "Showing nearing limit prompt");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Sắp đạt giới hạn!")
                .setMessage("Bạn đã sử dụng " + stats.lessonsToday + "/" + stats.maxLessonsPerDay +
                        " bài giảng hôm nay.\n\n" +
                        "Đăng ký để tiếp tục học không giới hạn!")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .setNegativeButton("Tiếp tục dùng thử", null)
                .show();
    }

    /**
     * 🎯 Hiển thị prompt engagement cho người dùng tích cực
     */
    private void showEngagementPrompt(Activity activity, GuestUsageTracker.UsageStats stats) {
        Log.d(TAG, "Showing engagement prompt");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bạn đang học rất tích cực! 🎉")
                .setMessage("Bạn đã sử dụng app " + stats.totalSessions + " lần.\n\n" +
                        "Đăng ký để lưu tiến trình và trải nghiệm đầy đủ:")
                .setPositiveButton("Đăng ký ngay", (dialog, which) -> {
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .setNegativeButton("Có thể sau", null)
                .show();
    }

    /**
     * 🎯 Tạo Usage Progress View
     */
    public View createUsageProgressView(Activity activity) {
        GuestUsageTracker.UsageStats stats = usageTracker.getTodayStats();
        GuestUsageTracker.RemainingUsage remaining = usageTracker.getRemainingUsage();

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Lesson progress
        addProgressItem(layout, "Bài giảng hôm nay",
                stats.lessonsToday, stats.maxLessonsPerDay);

        // Translation progress
        addProgressItem(layout, "Dịch thuật hôm nay",
                stats.translationsToday, stats.maxTranslationsPerDay);

        // Upgrade button
        Button upgradeBtn = new Button(activity);
        upgradeBtn.setText("Nâng cấp tài khoản");
        upgradeBtn.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, RegisterActivity.class));
        });
        layout.addView(upgradeBtn);

        return layout;
    }

    /**
     * 🎯 Thêm progress item
     */
    private void addProgressItem(LinearLayout parent, String title, int current, int max) {
        Context context = parent.getContext();

        TextView titleView = new TextView(context);
        titleView.setText(title + ": " + current + "/" + max);
        parent.addView(titleView);

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(max);
        progressBar.setProgress(current);
        parent.addView(progressBar);
    }

    /**
     * 🎯 Hiển thị welcome tips cho lần đầu sử dụng
     */
    public void showWelcomeTips(Activity activity) {
        SessionManager sessionManager = new SessionManager(activity);

        // Chỉ hiển thị cho guest mới
        if (sessionManager.isGuestMode()) {
            GuestUsageTracker.UsageStats stats = usageTracker.getTodayStats();

            if (stats.totalSessions <= 1) {
                showFirstTimeWelcome(activity);
            }
        }
    }

    /**
     * 🎯 Hiển thị welcome cho lần đầu
     */
    private void showFirstTimeWelcome(Activity activity) {
        Log.d(TAG, "Showing first time welcome");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chào mừng bạn! 👋")
                .setMessage("Bạn có thể trải nghiệm miễn phí:\n\n" +
                        "• 3 bài giảng mỗi ngày\n" +
                        "• 5 từ vựng mỗi bài\n" +
                        "Đăng ký để có trải nghiệm đầy đủ!")
                .setPositiveButton("Bắt đầu học", null)
                .setNegativeButton("Đăng ký ngay", (dialog, which) -> {
                    activity.startActivity(new Intent(activity, RegisterActivity.class));
                })
                .show();
    }

    /**
     *  Lấy usage tracker
     */
    public GuestUsageTracker getUsageTracker() {
        return usageTracker;
    }
}