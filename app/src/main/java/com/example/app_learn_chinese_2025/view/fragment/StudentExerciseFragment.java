package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_learn_chinese_2025.R;

public class StudentExerciseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạm thời dùng layout đơn giản
        TextView textView = new TextView(requireContext());
        textView.setText("🚧 Bài tập trắc nghiệm\n\nĐang phát triển...");
        textView.setTextSize(18);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(32, 32, 32, 32);

        return textView;
    }
}