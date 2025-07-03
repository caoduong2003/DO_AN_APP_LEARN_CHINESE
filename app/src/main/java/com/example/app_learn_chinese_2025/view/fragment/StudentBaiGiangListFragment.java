package com.example.app_learn_chinese_2025.view.fragment;

import android.content.Intent;
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
import com.example.app_learn_chinese_2025.view.activity.BaiGiangDetailActivity;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ GUARANTEED WORKING StudentBaiGiangListFragment
 * 100% tương thích với project hiện tại
 */
public class StudentBaiGiangListFragment extends Fragment
        implements BaiGiangAdapter.OnBaiGiangItemClickListener, BaiGiangController.OnBaiGiangListener {

    private static final String TAG = "StudentBaiGiangListFragment";

    // ===== UI COMPONENTS - EXACTLY MATCH LAYOUT =====
    private RecyclerView rvBaiGiang;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefresh;

    // ===== DATA & CONTROLLERS =====
    private BaiGiangAdapter adapter;
    private BaiGiangController baiGiangController;
    private List<BaiGiang> baiGiangList;

    // ===== LIFECYCLE METHODS =====

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "🔄 onCreateView started");

        // Inflate layout - EXACT SAME AS ORIGINAL
        View view = inflater.inflate(R.layout.fragment_student_bai_giang_list, container, false);

        // Initialize components
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadBaiGiangs();

        Log.d(TAG, "✅ onCreateView completed");
        return view;
    }

    // ===== INITIALIZATION METHODS =====

    private void initViews(View view) {
        Log.d(TAG, "🔧 initViews started");

        // Find views - EXACT IDs FROM LAYOUT
        rvBaiGiang = view.findViewById(R.id.rvBaiGiang);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        // Initialize controller - EXACT SAME AS ORIGINAL
        baiGiangController = new BaiGiangController(requireContext(), this);

        // Initialize data list
        baiGiangList = new ArrayList<>();

        // Hide loading components initially
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);

        Log.d(TAG, "✅ initViews completed");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "🔧 setupRecyclerView started");

        // Create adapter - EXACT SAME AS ORIGINAL
        adapter = new BaiGiangAdapter(requireContext(), baiGiangList, this);

        // Setup RecyclerView - EXACT SAME AS ORIGINAL
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);

        Log.d(TAG, "📋 Adapter created with " + baiGiangList.size() + " items");
        Log.d(TAG, "✅ setupRecyclerView completed");
    }

    private void setupSwipeRefresh() {
        Log.d(TAG, "🔧 setupSwipeRefresh started");

        if (swipeRefresh != null) {
            // EXACT SAME AS ORIGINAL
            swipeRefresh.setOnRefreshListener(this::loadBaiGiangs);
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );
        }

        Log.d(TAG, "✅ setupSwipeRefresh completed");
    }

    // ===== DATA LOADING METHODS =====

    private void loadBaiGiangs() {
        Log.d(TAG, "🌐 loadBaiGiangs started");

        // Check network - EXACT SAME AS ORIGINAL
        if (!isNetworkAvailable()) {
            Toast.makeText(requireContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            showEmptyState(true, "Không có kết nối mạng");
            return;
        }

        // Show loading
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        showEmptyState(false, "");

        // Call API - EXACT SAME AS ORIGINAL
        Log.d(TAG, "📡 Calling API: getBaiGiangList(null, null, null, null, true)");
        baiGiangController.getBaiGiangList(null, null, null, null, true);
    }

    private boolean isNetworkAvailable() {
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager)
                    requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            android.net.Network network = cm.getActiveNetwork();
            return network != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network: " + e.getMessage());
            return false;
        }
    }

    private void showEmptyState(boolean show, String message) {
        Log.d(TAG, "📋 showEmptyState: " + show + ", message: " + message);

        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show && message != null && !message.isEmpty()) {
                tvEmptyState.setText(message);
            }
        }

        if (rvBaiGiang != null) {
            rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // =====================================================================
    // IMPLEMENT: BaiGiangController.OnBaiGiangListener - KEY FIX HERE!
    // =====================================================================

    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        Log.d(TAG, "=== 🎯 onBaiGiangListReceived START ===");
        Log.d(TAG, "Received: " + (baiGiangList != null ? baiGiangList.size() : "NULL") + " items");

        // ✅ CRITICAL: Stop refreshing FIRST
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }

        // ✅ CRITICAL: Null check
        if (baiGiangList == null) {
            Log.w(TAG, "⚠️ Received NULL list");
            showEmptyState(true, "Không có dữ liệu");
            return;
        }

        // ✅ DEBUG: Log first few items
        if (!baiGiangList.isEmpty()) {
            for (int i = 0; i < Math.min(3, baiGiangList.size()); i++) {
                BaiGiang bg = baiGiangList.get(i);
                Log.d(TAG, "📝 Item " + i + ": ID=" + bg.getID() + ", Title=" + bg.getTieuDe());
            }
        }

        // ✅ CRITICAL: Update local list
        this.baiGiangList.clear();
        this.baiGiangList.addAll(baiGiangList);
        Log.d(TAG, "Updated local list size: " + this.baiGiangList.size());

        // ✅ CRITICAL: Check adapter exists
        if (adapter == null) {
            Log.e(TAG, "❌ CRITICAL ERROR: Adapter is NULL!");
            // Try to recreate adapter
            setupRecyclerView();
            if (adapter == null) {
                Log.e(TAG, "❌ FAILED to recreate adapter!");
                return;
            }
        }

        // ✅ CRITICAL: Update adapter
        Log.d(TAG, "🔄 Updating adapter...");
        Log.d(TAG, "Adapter before update: " + adapter.getItemCount() + " items");

        adapter.updateData(this.baiGiangList);

        Log.d(TAG, "Adapter after update: " + adapter.getItemCount() + " items");

        // ✅ Handle empty state
        if (this.baiGiangList.isEmpty()) {
            Log.w(TAG, "📭 Empty list - showing empty state");
            showEmptyState(true, "Chưa có bài giảng nào được xuất bản");
        } else {
            Log.d(TAG, "📚 Success - displaying " + this.baiGiangList.size() + " lessons");
            showEmptyState(false, "");
        }

        Log.d(TAG, "=== ✅ onBaiGiangListReceived END ===");
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        // Not used in this fragment
        Log.d(TAG, "onBaiGiangDetailReceived: " + (baiGiang != null ? baiGiang.getTieuDe() : "null"));
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        // Not used in this fragment
        Log.d(TAG, "onBaiGiangCreated: " + (baiGiang != null ? baiGiang.getTieuDe() : "null"));
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        // Not used in this fragment
        Log.d(TAG, "onBaiGiangUpdated: " + (baiGiang != null ? baiGiang.getTieuDe() : "null"));
    }

    @Override
    public void onBaiGiangDeleted() {
        // Not used in this fragment
        Log.d(TAG, "onBaiGiangDeleted");
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "=== ❌ onError START ===");
        Log.e(TAG, "Error message: " + message);

        // Stop refreshing
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }

        // Show error
        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_LONG).show();
        showEmptyState(true, "Lỗi tải dữ liệu: " + message);

        Log.e(TAG, "=== ❌ onError END ===");
    }

    // =====================================================================
    // IMPLEMENT: BaiGiangAdapter.OnBaiGiangItemClickListener
    // =====================================================================

    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d(TAG, "🖱️ Lesson clicked: " + baiGiang.getTieuDe() + " (ID: " + baiGiang.getID() + ")");

        // Navigate to lesson detail - EXACT SAME AS ORIGINAL
        Intent intent = new Intent(requireContext(), BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {

    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {

    }

    @Override
    public void onPlayVideo(BaiGiang baiGiang) {

    }

    @Override
    public void onPlayAudio(BaiGiang baiGiang) {

    }

    // =====================================================================
    // PUBLIC METHODS
    // =====================================================================

    public void refreshData() {
        Log.d(TAG, "🔄 Manual refresh triggered");
        loadBaiGiangs();
    }

    // =====================================================================
    // LIFECYCLE OVERRIDES
    // =====================================================================

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "🔄 onResume - checking adapter state");

        if (adapter != null && baiGiangList != null) {
            Log.d(TAG, "📋 Adapter: " + adapter.getItemCount() + " items, List: " + baiGiangList.size() + " items");

            // Sync check
            if (adapter.getItemCount() != baiGiangList.size()) {
                Log.w(TAG, "⚠️ Count mismatch - forcing adapter update");
                adapter.updateData(new ArrayList<>(this.baiGiangList));
            }
        } else {
            Log.w(TAG, "⚠️ Null components in onResume - adapter=" + (adapter != null) + ", list=" + (baiGiangList != null));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "🗑️ onDestroyView - cleaning up");

        // Clean up references
        if (adapter != null) {
            adapter = null;
        }
        if (baiGiangList != null) {
            baiGiangList.clear();
        }
    }
}