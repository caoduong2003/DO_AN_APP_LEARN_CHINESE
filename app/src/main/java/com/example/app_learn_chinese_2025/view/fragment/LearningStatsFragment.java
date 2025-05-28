package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.TienTrinhRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

public class LearningStatsFragment extends Fragment {
    private TextView tvTotalLessons, tvCompletedLessons, tvOverallProgress;
    private TextView tvMotivationMessage;
    private ProgressBar progressBarOverall;

    private SessionManager sessionManager;
    private TienTrinhRepository tienTrinhRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning_stats, container, false);

        initViews(view);
        loadStats();

        return view;
    }

    private void initViews(View view) {
        tvTotalLessons = view.findViewById(R.id.tvTotalLessons);
        tvCompletedLessons = view.findViewById(R.id.tvCompletedLessons);
        tvOverallProgress = view.findViewById(R.id.tvOverallProgress);
        tvMotivationMessage = view.findViewById(R.id.tvMotivationMessage);
        progressBarOverall = view.findViewById(R.id.progressBarOverall);

        sessionManager = new SessionManager(requireContext());
        tienTrinhRepository = new TienTrinhRepository(sessionManager);
    }

    private void loadStats() {
        User user = sessionManager.getUserDetails();
        if (user == null) {
            showDefaultStats();
            return;
        }

        tienTrinhRepository.getTienTrinhByUser(user.getID(), new TienTrinhRepository.OnTienTrinhListCallback() {
            @Override
            public void onSuccess(List<TienTrinh> tienTrinhs) {
                calculateAndDisplayStats(tienTrinhs);
            }

            @Override
            public void onError(String errorMessage) {
                showDefaultStats();
            }
        });
    }

    private void calculateAndDisplayStats(List<TienTrinh> tienTrinhs) {
        int totalLessons = tienTrinhs.size();
        int completedLessons = 0;
        int totalProgress = 0;

        for (TienTrinh tienTrinh : tienTrinhs) {
            if (tienTrinh.isDaHoanThanh()) {
                completedLessons++;
            }
            totalProgress += tienTrinh.getTienDo();
        }

        int overallProgress = totalLessons > 0 ? totalProgress / totalLessons : 0;

        // Update UI on main thread
        if (getActivity() != null) {
            int finalCompletedLessons = completedLessons;
            getActivity().runOnUiThread(() -> {
                tvTotalLessons.setText(String.valueOf(totalLessons));
                tvCompletedLessons.setText(String.valueOf(finalCompletedLessons));
                tvOverallProgress.setText(overallProgress + "%");
                progressBarOverall.setProgress(overallProgress);

                // Show motivational message
                String message = getMotivationMessage(finalCompletedLessons, totalLessons, overallProgress);
                tvMotivationMessage.setText(message);
            });
        }
    }

    private String getMotivationMessage(int completed, int total, int progress) {
        if (total == 0) {
            return "🌟 Hãy bắt đầu hành trình học tiếng Trung của bạn!";
        } else if (completed == 0) {
            return "💪 Bạn có " + total + " bài học đang chờ. Hãy bắt đầu ngay!";
        } else if (progress < 25) {
            return "🎯 Khởi đầu tốt! Tiếp tục nỗ lực nhé!";
        } else if (progress < 50) {
            return "🔥 Tuyệt vời! Bạn đang có tiến bộ rõ rệt!";
        } else if (progress < 75) {
            return "⭐ Xuất sắc! Bạn đã hoàn thành hơn một nửa!";
        } else if (progress < 100) {
            return "🏆 Gần đến đích rồi! Cố gắng lên!";
        } else {
            return "🎉 Chúc mừng! Bạn đã hoàn thành tất cả bài học!";
        }
    }

    private void showDefaultStats() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvTotalLessons.setText("0");
                tvCompletedLessons.setText("0");
                tvOverallProgress.setText("0%");
                progressBarOverall.setProgress(0);
                tvMotivationMessage.setText("🌟 Chào mừng bạn đến với ứng dụng học tiếng Trung!");
            });
        }
    }
}