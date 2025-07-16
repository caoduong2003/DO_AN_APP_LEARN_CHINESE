package com.example.app_learn_chinese_2025.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app_learn_chinese_2025.model.data.VisualLearning;

/**
 * 🗄️ App Database - Đơn giản và dễ hiểu
 */
@Database(entities = {VisualLearning.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "visual_learning_database";
    private static AppDatabase instance;

    // Abstract method để lấy DAO
    public abstract VisualLearningDao visualLearningDao();

    // Singleton pattern
    public static synchronized AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration() // Đơn giản hóa cho development
                    .build();
        }
        return instance;
    }
}