package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.model.repository.MauCauRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.MauCauAdapter;

import java.util.ArrayList;
import java.util.List;

public class MauCauListFragment extends Fragment {
    private RecyclerView rvMauCau;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private MauCauRepository mauCauRepository;
    private MauCauAdapter adapter;

    private long baiGiangId;
    private List<MauCau> mauCauList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get baiGiangId from arguments
        if (getArguments() != null) {
            baiGiangId = getArguments().getLong("BAI_GIANG_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mau_cau_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        // Load data
        loadMauCauList();

        return view;
    }

    private void initViews(View view) {
        rvMauCau = view.findViewById(R.id.rvMauCau);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(requireContext());
        mauCauRepository = new MauCauRepository(requireContext(), sessionManager);

        mauCauList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new MauCauAdapter(requireContext(), mauCauList);
        rvMauCau.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMauCau.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadMauCauList);
    }

    private void loadMauCauList() {
        if (baiGiangId <= 0) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);

        mauCauRepository.getMauCauByBaiGiang(baiGiangId, new MauCauRepository.OnMauCauListCallback() {
            @Override
            public void onSuccess(List<MauCau> mauCaus) {
                mauCauList = mauCaus;
                adapter.updateData(mauCaus);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.release(); // Release MediaPlayer resources if any
        }
    }
}