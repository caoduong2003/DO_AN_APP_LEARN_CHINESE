package com.example.app_learn_chinese_2025.model.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 📚 Entity cho Visual Learning - Học từ vựng qua hình ảnh
 * Sử dụng cấu trúc từ code demo nhưng được tối ưu hóa
 */
@Entity(tableName = "visual_learning")
public class VisualLearning {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "hoc_vien_id")
    private long hocVienId;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "detected_object")
    private String detectedObject;

    @ColumnInfo(name = "chinese_vocabulary")
    private String chineseVocabulary;

    @ColumnInfo(name = "pinyin")
    private String pinyin;

    @ColumnInfo(name = "vietnamese_meaning")
    private String vietnameseMeaning;

    @ColumnInfo(name = "example_sentence")
    private String exampleSentence;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    // Constructors
    public VisualLearning() {
        this.createdAt = System.currentTimeMillis();
        this.isFavorite = false;
    }

    public VisualLearning(long hocVienId, String imagePath, String detectedObject,
                          String chineseVocabulary, String pinyin, String vietnameseMeaning,
                          String exampleSentence) {
        this();
        this.hocVienId = hocVienId;
        this.imagePath = imagePath;
        this.detectedObject = detectedObject;
        this.chineseVocabulary = chineseVocabulary;
        this.pinyin = pinyin;
        this.vietnameseMeaning = vietnameseMeaning;
        this.exampleSentence = exampleSentence;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHocVienId() { return hocVienId; }
    public void setHocVienId(long hocVienId) { this.hocVienId = hocVienId; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDetectedObject() { return detectedObject; }
    public void setDetectedObject(String detectedObject) { this.detectedObject = detectedObject; }

    public String getChineseVocabulary() { return chineseVocabulary; }
    public void setChineseVocabulary(String chineseVocabulary) { this.chineseVocabulary = chineseVocabulary; }

    public String getPinyin() { return pinyin; }
    public void setPinyin(String pinyin) { this.pinyin = pinyin; }

    public String getVietnameseMeaning() { return vietnameseMeaning; }
    public void setVietnameseMeaning(String vietnameseMeaning) { this.vietnameseMeaning = vietnameseMeaning; }

    public String getExampleSentence() { return exampleSentence; }
    public void setExampleSentence(String exampleSentence) { this.exampleSentence = exampleSentence; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    // Helper methods
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(createdAt));
    }

    @Override
    public String toString() {
        return "VisualLearning{" +
                "id=" + id +
                ", detectedObject='" + detectedObject + '\'' +
                ", chineseVocabulary='" + chineseVocabulary + '\'' +
                ", vietnameseMeaning='" + vietnameseMeaning + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}