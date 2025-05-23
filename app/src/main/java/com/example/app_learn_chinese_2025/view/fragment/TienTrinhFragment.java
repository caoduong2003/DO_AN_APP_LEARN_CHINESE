package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.TienTrinhRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.TienTrinhAdapter;

import java.util.ArrayList;
import java.util.List;

public class TienTrinhFragment extends Fragment {
    private TextView tvTongTienTrinh;
    private ProgressBar progressBarTongTienTrinh;
    private RecyclerView rvTienTrinh;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private TienTrinhRepository tienTrinhRepository;
    private TienTrinhAdapter adapter;

    private List<TienTrinh> tienTrinhList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tien_trinh, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        // Load data
        loadTienTrinh();

        return view;
    }

    private void initViews(View view) {
        tvTongTienTrinh = view.findViewById(R.id.tvTongTienTrinh);
        progressBarTongTienTrinh = view.findViewById(R.id.progressBarTongTienTrinh);
        rvTienTrinh = view.findViewById(R.id.rvTienTrinh);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(requireContext());
        tienTrinhRepository = new TienTrinhRepository(sessionManager);
        tienTrinhList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new TienTrinhAdapter(requireContext(), tienTrinhList);
        rvTienTrinh.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTienTrinh.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadTienTrinh);
    }

    private void loadTienTrinh() {
        swipeRefresh.setRefreshing(true);

        // Get current user ID
        User user = sessionManager.getUserDetails();
        if (user == null) {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(requireContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        tienTrinhRepository.getTienTrinhByUser(user.getID(), new TienTrinhRepository.OnTienTrinhListCallback() {
            @Override
            public void onSuccess(List<TienTrinh> tienTrinhs) {
                tienTrinhList = tienTrinhs;
                adapter.updateData(tienTrinhs);
                swipeRefresh.setRefreshing(false);

                // Calculate overall progress
                calculateOverallProgress(tienTrinhs);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void calculateOverallProgress(List<TienTrinh> tienTrinhs) {
        int totalBaiGiang = tienTrinhs.size();
        int completedBaiGiang = 0;

        for (TienTrinh tienTrinh : tienTrinhs) {
            if (tienTrinh.isDaHoanThanh()) {
                completedBaiGiang++;
            }
        }

        int progressPercentage = totalBaiGiang > 0 ? (completedBaiGiang * 100) / totalBaiGiang : 0;

        // Update UI
        tvTongTienTrinh.setText("Tiến trình tổng thể: " + progressPercentage + "% (" + completedBaiGiang + "/" + totalBaiGiang + ")");
        progressBarTongTienTrinh.setProgress(progressPercentage);
    }
}