package com.example.app_learn_chinese_2025.model.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.app_learn_chinese_2025.model.data.VisualLearning;
import com.example.app_learn_chinese_2025.model.database.AppDatabase;
import com.example.app_learn_chinese_2025.model.database.VisualLearningDao;

import java.util.List;

public class VisualLearningRepository {
    private VisualLearningDao visualLearningDao;

    public VisualLearningRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        visualLearningDao = database.visualLearningDao();
    }

    // Get methods
    public LiveData<List<VisualLearning>> getAllByHocVien(long hocVienId) {
        return visualLearningDao.getAllByHocVien(hocVienId);
    }

    public LiveData<List<VisualLearning>> getFavorites(long hocVienId) {
        return visualLearningDao.getFavoritesByHocVien(hocVienId);
    }

    public LiveData<List<VisualLearning>> getRecent(long hocVienId, int limit) {
        return visualLearningDao.getRecentByHocVien(hocVienId, limit);
    }

    public LiveData<Integer> getCount(long hocVienId) {
        return visualLearningDao.getCountByHocVien(hocVienId);
    }

    public LiveData<List<VisualLearning>> search(String keyword) {
        return visualLearningDao.searchByKeyword(keyword);
    }

    // Insert method
    public void insert(VisualLearning visualLearning, OnInsertCompleteListener listener) {
        new Thread(() -> {
            long id = visualLearningDao.insert(visualLearning);
            if (listener != null) {
                new Handler(Looper.getMainLooper()).post(() -> listener.onInsertComplete(id));
            }
        }).start();
    }

    // Update methods
    public void update(VisualLearning visualLearning) {
        new Thread(() -> visualLearningDao.update(visualLearning)).start();
    }

    public void updateFavorite(long id, boolean isFavorite) {
        new Thread(() -> visualLearningDao.updateFavoriteStatus(id, isFavorite)).start();
    }

    // Delete methods
    public void delete(VisualLearning visualLearning) {
        new Thread(() -> visualLearningDao.delete(visualLearning)).start();
    }

    public void deleteAll(long hocVienId) {
        new Thread(() -> visualLearningDao.deleteAllByHocVien(hocVienId)).start();
    }

    // Callback interface
    public interface OnInsertCompleteListener {
        void onInsertComplete(long id);
    }
}