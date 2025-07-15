package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class GuestUsageTracker {
    private static final String TAG = "GuestUsageTracker";
    private static final String PREF_NAME = "guest_usage_tracker";

    // Usage limits
    private static final int MAX_LESSONS_PER_DAY = 5;
    private static final int MAX_VOCABULARY_PER_LESSON = 5;
    private static final int MAX_TRANSLATIONS_PER_DAY = 10;

    // Preference keys
    private static final String KEY_LESSON_COUNT = "lesson_count_";
    private static final String KEY_VOCABULARY_COUNT = "vocabulary_count_";
    private static final String KEY_TRANSLATION_COUNT = "translation_count_";
    private static final String KEY_LAST_RESET = "last_reset";
    private static final String KEY_FIRST_USAGE = "first_usage";
    private static final String KEY_TOTAL_SESSIONS = "total_sessions";

    private SharedPreferences prefs;
    private Context context;

    public GuestUsageTracker(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        resetIfNewDay();
        recordSession();
    }

    /**
     *  Kiểm tra có thể truy cập bài giảng không
     */
    public boolean canAccessLesson() {
        int count = getTodayLessonCount();
        boolean canAccess = count < MAX_LESSONS_PER_DAY;
        Log.d(TAG, "Can access lesson: " + canAccess + " (count: " + count + "/" + MAX_LESSONS_PER_DAY + ")");
        return canAccess;
    }

    /**
     *  Ghi nhận truy cập bài giảng
     */
    public void recordLessonAccess() {
        int count = getTodayLessonCount();
        prefs.edit().putInt(KEY_LESSON_COUNT + getTodayKey(), count + 1).apply();
        Log.d(TAG, "✅ Recorded lesson access. New count: " + (count + 1));
    }

    /**
     *  Kiểm tra có thể xem từ vựng không
     */
    public boolean canAccessVocabulary(long baiGiangId) {
        int count = getTodayVocabularyCount(baiGiangId);
        boolean canAccess = count < MAX_VOCABULARY_PER_LESSON;
        Log.d(TAG, "Can access vocabulary for lesson " + baiGiangId + ": " + canAccess +
                " (count: " + count + "/" + MAX_VOCABULARY_PER_LESSON + ")");
        return canAccess;
    }

    /**
     * Ghi nhận truy cập từ vựng
     */
    public void recordVocabularyAccess(long baiGiangId) {
        int count = getTodayVocabularyCount(baiGiangId);
        prefs.edit().putInt(KEY_VOCABULARY_COUNT + getTodayKey() + "_" + baiGiangId, count + 1).apply();
        Log.d(TAG, "✅ Recorded vocabulary access for lesson " + baiGiangId + ". New count: " + (count + 1));
    }

    /**
     *  Kiểm tra có thể dịch thuật không
     */
    public boolean canTranslate() {
        int count = getTodayTranslationCount();
        boolean canAccess = count < MAX_TRANSLATIONS_PER_DAY;
        Log.d(TAG, "Can translate: " + canAccess + " (count: " + count + "/" + MAX_TRANSLATIONS_PER_DAY + ")");
        return canAccess;
    }

    /**
     *  Ghi nhận sử dụng dịch thuật
     */
    public void recordTranslationUsage() {
        int count = getTodayTranslationCount();
        prefs.edit().putInt(KEY_TRANSLATION_COUNT + getTodayKey(), count + 1).apply();
        Log.d(TAG, "✅ Recorded translation usage. New count: " + (count + 1));
    }

    /**
     *  Lấy thống kê usage hôm nay
     */
    public UsageStats getTodayStats() {
        return new UsageStats(
                getTodayLessonCount(),
                MAX_LESSONS_PER_DAY,
                getTodayTranslationCount(),
                MAX_TRANSLATIONS_PER_DAY,
                getTotalSessions(),
                getFirstUsageDate()
        );
    }

    /**
     * Lấy remaining usage
     */
    public RemainingUsage getRemainingUsage() {
        return new RemainingUsage(
                MAX_LESSONS_PER_DAY - getTodayLessonCount(),
                MAX_VOCABULARY_PER_LESSON, // Per lesson
                MAX_TRANSLATIONS_PER_DAY - getTodayTranslationCount()
        );
    }

    /**
     * Reset nếu sang ngày mới
     */
    private void resetIfNewDay() {
        String today = getTodayKey();
        String lastReset = prefs.getString(KEY_LAST_RESET, "");

        if (!today.equals(lastReset)) {
            Log.d(TAG, "🔄 Resetting usage for new day: " + today);
            prefs.edit()
                    .putString(KEY_LAST_RESET, today)
                    .putInt(KEY_LESSON_COUNT + today, 0)
                    .putInt(KEY_TRANSLATION_COUNT + today, 0)
                    .apply();
        }
    }

    /**
     * Lấy key cho ngày hôm nay
     */
    private String getTodayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    /**
     * Lấy số bài giảng đã xem hôm nay
     */
    private int getTodayLessonCount() {
        return prefs.getInt(KEY_LESSON_COUNT + getTodayKey(), 0);
    }

    /**
     * Lấy số từ vựng đã xem hôm nay cho bài giảng
     */
    private int getTodayVocabularyCount(long baiGiangId) {
        return prefs.getInt(KEY_VOCABULARY_COUNT + getTodayKey() + "_" + baiGiangId, 0);
    }

    /**
     * Lấy số lần dịch thuật hôm nay
     */
    private int getTodayTranslationCount() {
        return prefs.getInt(KEY_TRANSLATION_COUNT + getTodayKey(), 0);
    }

    /**
     * Ghi nhận session mới
     */
    private void recordSession() {
        if (prefs.getLong(KEY_FIRST_USAGE, 0) == 0) {
            prefs.edit().putLong(KEY_FIRST_USAGE, System.currentTimeMillis()).apply();
        }

        int totalSessions = prefs.getInt(KEY_TOTAL_SESSIONS, 0);
        prefs.edit().putInt(KEY_TOTAL_SESSIONS, totalSessions + 1).apply();

        Log.d(TAG, "📊 Session recorded. Total sessions: " + (totalSessions + 1));
    }

    /**
     * Lấy tổng số sessions
     */
    private int getTotalSessions() {
        return prefs.getInt(KEY_TOTAL_SESSIONS, 0);
    }

    /**
     *  Lấy ngày sử dụng đầu tiên
     */
    private String getFirstUsageDate() {
        long firstUsage = prefs.getLong(KEY_FIRST_USAGE, 0);
        if (firstUsage == 0) return "Chưa xác định";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(firstUsage));
    }

    /**
     * 🗑 Clear tất cả usage data
     */
    public void clearUsageData() {
        Log.d(TAG, "🗑️ Clearing all usage data");
        prefs.edit().clear().apply();
    }

    /**
     * Usage Statistics Class
     */
    public static class UsageStats {
        public final int lessonsToday;
        public final int maxLessonsPerDay;
        public final int translationsToday;
        public final int maxTranslationsPerDay;
        public final int totalSessions;
        public final String firstUsageDate;

        public UsageStats(int lessonsToday, int maxLessonsPerDay,
                          int translationsToday, int maxTranslationsPerDay,
                          int totalSessions, String firstUsageDate) {
            this.lessonsToday = lessonsToday;
            this.maxLessonsPerDay = maxLessonsPerDay;
            this.translationsToday = translationsToday;
            this.maxTranslationsPerDay = maxTranslationsPerDay;
            this.totalSessions = totalSessions;
            this.firstUsageDate = firstUsageDate;
        }

        public double getLessonUsagePercentage() {
            return maxLessonsPerDay == 0 ? 0 : (lessonsToday * 100.0) / maxLessonsPerDay;
        }

        public double getTranslationUsagePercentage() {
            return maxTranslationsPerDay == 0 ? 0 : (translationsToday * 100.0) / maxTranslationsPerDay;
        }

        public boolean isNearingLimit() {
            return getLessonUsagePercentage() >= 80 || getTranslationUsagePercentage() >= 80;
        }
    }

    /**
     *  Remaining Usage Class
     */
    public static class RemainingUsage {
        public final int remainingLessons;
        public final int vocabularyPerLesson;
        public final int remainingTranslations;

        public RemainingUsage(int remainingLessons, int vocabularyPerLesson,
                              int remainingTranslations) {
            this.remainingLessons = Math.max(0, remainingLessons);
            this.vocabularyPerLesson = vocabularyPerLesson;
            this.remainingTranslations = Math.max(0, remainingTranslations);
        }

        public boolean hasRemainingLessons() {
            return remainingLessons > 0;
        }

        public boolean hasRemainingTranslations() {
            return remainingTranslations > 0;
        }
    }
}