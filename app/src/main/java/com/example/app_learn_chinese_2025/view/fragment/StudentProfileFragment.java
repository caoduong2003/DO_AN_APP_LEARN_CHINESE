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

public class StudentProfileFragment extends Fragment {

    private TextView tvProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        initViews(view);
        loadProfileData();

        return view;
    }

    private void initViews(View view) {
        tvProfile = view.findViewById(R.id.tvProfile);
    }

    private void loadProfileData() {
        // TODO: Load actual profile data
        tvProfile.setText("Thông tin hồ sơ của bạn sẽ hiển thị ở đây");
    }

    public void refreshData() {
        loadProfileData();
    }
}