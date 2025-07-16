package com.example.app_learn_chinese_2025.model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_learn_chinese_2025.model.data.VisualLearning;

import java.util.List;

/**
 * 🗄️ DAO cho VisualLearning - Đơn giản và dễ hiểu
 */
@Dao
public interface VisualLearningDao {

    // ===== INSERT =====
    @Insert
    long insert(VisualLearning visualLearning);

    // ===== UPDATE =====
    @Update
    void update(VisualLearning visualLearning);

    @Query("UPDATE visual_learning SET is_favorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(long id, boolean isFavorite);

    // ===== DELETE =====
    @Delete
    void delete(VisualLearning visualLearning);

    @Query("DELETE FROM visual_learning WHERE hoc_vien_id = :hocVienId")
    void deleteAllByHocVien(long hocVienId);

    // ===== SELECT =====
    @Query("SELECT * FROM visual_learning WHERE hoc_vien_id = :hocVienId ORDER BY created_at DESC")
    LiveData<List<VisualLearning>> getAllByHocVien(long hocVienId);

    @Query("SELECT * FROM visual_learning WHERE hoc_vien_id = :hocVienId AND is_favorite = 1 ORDER BY created_at DESC")
    LiveData<List<VisualLearning>> getFavoritesByHocVien(long hocVienId);

    @Query("SELECT * FROM visual_learning WHERE hoc_vien_id = :hocVienId ORDER BY created_at DESC LIMIT :limit")
    LiveData<List<VisualLearning>> getRecentByHocVien(long hocVienId, int limit);

    @Query("SELECT COUNT(*) FROM visual_learning WHERE hoc_vien_id = :hocVienId")
    LiveData<Integer> getCountByHocVien(long hocVienId);

    @Query("SELECT * FROM visual_learning WHERE " +
            "detected_object LIKE '%' || :keyword || '%' OR " +
            "chinese_vocabulary LIKE '%' || :keyword || '%' OR " +
            "vietnamese_meaning LIKE '%' || :keyword || '%' " +
            "ORDER BY created_at DESC")
    LiveData<List<VisualLearning>> searchByKeyword(String keyword);
}