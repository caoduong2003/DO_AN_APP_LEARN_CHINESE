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

public class StudentProgressFragment extends Fragment {

    private TextView tvProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_progress, container, false);

        initViews(view);
        loadProgressData();

        return view;
    }

    private void initViews(View view) {
        tvProgress = view.findViewById(R.id.tvProgress);
    }

    private void loadProgressData() {
        // TODO: Load actual progress data
        tvProgress.setText("Tiến trình học tập của bạn sẽ hiển thị ở đây");
    }

    public void refreshData() {
        loadProgressData();
    }
}