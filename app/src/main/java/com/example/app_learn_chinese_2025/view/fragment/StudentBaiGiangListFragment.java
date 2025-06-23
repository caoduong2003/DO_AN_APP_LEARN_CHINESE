package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.view.activity.StudentDashboardActivity;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentBaiGiangListFragment extends Fragment implements BaiGiangAdapter.OnBaiGiangItemClickListener, BaiGiangController.OnBaiGiangListener {
    private static final String TAG = "StudentBaiGiangListFragment";

    private RecyclerView rvBaiGiang;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefresh;
    private BaiGiangAdapter adapter;
    private BaiGiangController baiGiangController;
    private List<BaiGiang> baiGiangList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_bai_giang_list, container, false);
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadBaiGiangs();
        return view;
    }

    private void initViews(View view) {
        rvBaiGiang = view.findViewById(R.id.rvBaiGiang);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        baiGiangController = new BaiGiangController(requireContext(), this);
        baiGiangList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new BaiGiangAdapter(requireContext(), baiGiangList, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadBaiGiangs);
    }

    private void loadBaiGiangs() {
        if (!isNetworkAvailable()) {
            Toast.makeText(requireContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            showEmptyState(true, "Không có kết nối mạng");
            return;
        }

        swipeRefresh.setRefreshing(true);
        showEmptyState(false, "");
        baiGiangController.getBaiGiangList(null, null, null, null, true); // Load published lessons
    }

    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        android.net.Network network = cm.getActiveNetwork();
        return network != null;
    }

    private void showEmptyState(boolean show, String message) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        tvEmptyState.setText(message);
        rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // BaiGiangAdapter.OnBaiGiangItemClickListener implementation
    @Override
    public void onItemClick(BaiGiang baiGiang) {
        if (getActivity() instanceof StudentDashboardActivity) {
            ((StudentDashboardActivity) getActivity()).navigateToLessonDetail(baiGiang.getID());
        }
    }

    // BaiGiangController.OnBaiGiangListener implementations
    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        swipeRefresh.setRefreshing(false);
        this.baiGiangList.clear();
        if (baiGiangList != null) {
            this.baiGiangList.addAll(baiGiangList);
        }
        adapter.updateData(this.baiGiangList);
        if (baiGiangList == null || baiGiangList.isEmpty()) {
            showEmptyState(true, "Không có bài giảng nào");
        } else {
            showEmptyState(false, "");
        }
        Log.d(TAG, "Received " + baiGiangList.size() + " lessons");
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangDeleted() {
        // Not used in this fragment
    }

    @Override
    public void onError(String message) {
        swipeRefresh.setRefreshing(false);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        showEmptyState(true, "Lỗi tải dữ liệu: " + message);
        Log.e(TAG, "Error: " + message);
    }

    public void refreshData() {
        loadBaiGiangs();
    }
}