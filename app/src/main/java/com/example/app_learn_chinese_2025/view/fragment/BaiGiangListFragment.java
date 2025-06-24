package com.example.app_learn_chinese_2025.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.activity.BaiGiangDetailActivity;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class BaiGiangListFragment extends Fragment implements BaiGiangAdapter.OnBaiGiangItemClickListener {

    private static final String TAG = "BaiGiangListFragment";

    // UI Components
    private Spinner spinnerCapDoHSK, spinnerChuDe;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState, tvResultCount;
    private ChipGroup chipGroupViewMode;
    private Chip chipListView, chipGridView;

    // Data & Controllers
    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private BaiGiangAdapter adapter;

    private List<BaiGiang> baiGiangList;
    private List<BaiGiang> filteredList;
    private List<CapDoHSK> capDoHSKList;
    private List<ChuDe> chuDeList;

    // State
    private int selectedCapDoHSKPosition = 0;
    private int selectedChuDePosition = 0;
    private boolean isGridView = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bai_giang_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        setupViewModeChips();

        // Load data
        loadSpinnerData();
        loadBaiGiangs(null, null, null, null); // Load all initially

        return view;
    }

    private void initViews(View view) {
        spinnerCapDoHSK = view.findViewById(R.id.spinnerCapDoHSK);
        spinnerChuDe = view.findViewById(R.id.spinnerChuDe);
        rvBaiGiang = view.findViewById(R.id.rvBaiGiang);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvResultCount = view.findViewById(R.id.tvResultCount);
        chipGroupViewMode = view.findViewById(R.id.chipGroupViewMode);
        chipListView = view.findViewById(R.id.chipListView);
        chipGridView = view.findViewById(R.id.chipGridView);

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(requireContext(), sessionManager);

        baiGiangList = new ArrayList<>();
        filteredList = new ArrayList<>();
        capDoHSKList = new ArrayList<>();
        chuDeList = new ArrayList<>();

        Log.d(TAG, "Fragment initialized");
    }

    private void setupRecyclerView() {
        adapter = new BaiGiangAdapter(requireContext(), filteredList, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);

        Log.d(TAG, "RecyclerView setup completed");
    }

    private void setupViewModeChips() {
        if (chipGroupViewMode != null) {
            chipListView.setOnClickListener(v -> {
                if (isGridView) {
                    toggleViewMode();
                }
            });

            chipGridView.setOnClickListener(v -> {
                if (!isGridView) {
                    toggleViewMode();
                }
            });

            // Set default selection
            chipListView.setChecked(true);
        }
    }

    private void toggleViewMode() {
        isGridView = !isGridView;

        if (isGridView) {
            rvBaiGiang.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            if (chipGridView != null) chipGridView.setChecked(true);
            if (chipListView != null) chipListView.setChecked(false);
        } else {
            rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
            if (chipListView != null) chipListView.setChecked(true);
            if (chipGridView != null) chipGridView.setChecked(false);
        }

        adapter.notifyDataSetChanged();
        Log.d(TAG, "View mode toggled to: " + (isGridView ? "Grid" : "List"));
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            Log.d(TAG, "Refresh triggered");
            applyFilters();
        });

        // Spinner listeners
        spinnerCapDoHSK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedCapDoHSKPosition != position) {
                    selectedCapDoHSKPosition = position;
                    Log.d(TAG, "CapDoHSK filter changed to position: " + position);
                    applyFilters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerChuDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedChuDePosition != position) {
                    selectedChuDePosition = position;
                    Log.d(TAG, "ChuDe filter changed to position: " + position);
                    applyFilters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadSpinnerData() {
        Log.d(TAG, "Loading spinner data");

        // Load CapDoHSK data
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                Log.d(TAG, "Loaded " + capDoHSKs.size() + " CapDoHSK items");
                capDoHSKList = capDoHSKs;
                setupCapDoHSKSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading CapDoHSK: " + errorMessage);
                Toast.makeText(requireContext(), "Lỗi tải cấp độ HSK: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Load ChuDe data
        baiGiangRepository.getAllChuDe(new BaiGiangRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDes) {
                Log.d(TAG, "Loaded " + chuDes.size() + " ChuDe items");
                chuDeList = chuDes;
                setupChuDeSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading ChuDe: " + errorMessage);
                Toast.makeText(requireContext(), "Lỗi tải chủ đề: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCapDoHSKSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả cấp độ"); // Add "All" option

        for (CapDoHSK capDoHSK : capDoHSKList) {
            spinnerItems.add("HSK " + capDoHSK.getCapDo() + " - " + capDoHSK.getTenCapDo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapDoHSK.setAdapter(adapter);

        // Set default selection
        spinnerCapDoHSK.setSelection(0);
        Log.d(TAG, "CapDoHSK spinner setup completed with " + spinnerItems.size() + " items");
    }

    private void setupChuDeSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả chủ đề"); // Add "All" option

        for (ChuDe chuDe : chuDeList) {
            spinnerItems.add(chuDe.getTenChuDe());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChuDe.setAdapter(adapter);

        // Set default selection
        spinnerChuDe.setSelection(0);
        Log.d(TAG, "ChuDe spinner setup completed with " + spinnerItems.size() + " items");
    }

    private void applyFilters() {
        // Get selected filters
        Integer capDoHSK_ID = null;
        Integer chuDeId = null;

        if (selectedCapDoHSKPosition > 0 && selectedCapDoHSKPosition <= capDoHSKList.size()) {
            capDoHSK_ID = capDoHSKList.get(selectedCapDoHSKPosition - 1).getID(); // -1 because first item is "Tất cả"
        }

        if (selectedChuDePosition > 0 && selectedChuDePosition <= chuDeList.size()) {
            chuDeId = chuDeList.get(selectedChuDePosition - 1).getID(); // -1 because first item is "Tất cả"
        }

        Log.d(TAG, "Applying filters - CapDoHSK_ID: " + capDoHSK_ID + ", ChuDeId: " + chuDeId);

        // Load bai giang with filters
        loadBaiGiangs(null, null, capDoHSK_ID, chuDeId);
    }

    private void loadBaiGiangs(Integer loaiBaiGiangId, Long giangVienId, Integer capDoHSK_ID, Integer chuDeId) {
        swipeRefresh.setRefreshing(true);
        showEmptyState(false);

        // Only get published lessons
        Boolean published = true;

        Log.d(TAG, "Loading bài giảng with filters - loaiBaiGiangId: " + loaiBaiGiangId + ", giangVienId: " + giangVienId + ", capDoHSK_ID: " + capDoHSK_ID + ", chuDeId: " + chuDeId);

        baiGiangRepository.getAllBaiGiang(giangVienId, loaiBaiGiangId, capDoHSK_ID, chuDeId, published, new BaiGiangRepository.OnBaiGiangListCallback() {
            @Override
            public void onSuccess(List<BaiGiang> baiGiangs) {
                Log.d(TAG, "Successfully loaded " + baiGiangs.size() + " bài giảng");
                baiGiangList = baiGiangs;
                filteredList.clear();
                filteredList.addAll(baiGiangs);

                adapter.updateData(filteredList);
                updateResultCount();
                swipeRefresh.setRefreshing(false);

                // Show empty state if no results
                if (baiGiangs.isEmpty()) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading BaiGiang: " + errorMessage);
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_LONG).show();
                swipeRefresh.setRefreshing(false);
                showEmptyState(true);
            }
        });
    }

    private void updateResultCount() {
        if (tvResultCount != null) {
            int count = filteredList.size();
            String text = count + " bài giảng";
            if (selectedCapDoHSKPosition > 0 || selectedChuDePosition > 0) {
                text += " (đã lọc)";
            }
            tvResultCount.setText(text);
            tvResultCount.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);

            if (show) {
                if (selectedCapDoHSKPosition > 0 || selectedChuDePosition > 0) {
                    tvEmptyState.setText("Không tìm thấy bài giảng phù hợp với bộ lọc.\nThử thay đổi điều kiện lọc.");
                } else {
                    tvEmptyState.setText("Chưa có bài giảng nào.\nHãy quay lại sau!");
                }
            }
        }

        rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);

        if (tvResultCount != null) {
            tvResultCount.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d(TAG, "Clicked on bài giảng: " + baiGiang.getTieuDe());

        Intent intent = new Intent(requireContext(), BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        Log.d(TAG, "Edit click not available for students");
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        Log.d(TAG, "Delete click not available for students");
    }

    @Override
    public void onPlayVideo(BaiGiang baiGiang) {
        if (baiGiang.getVideoUrl() != null && !baiGiang.getVideoUrl().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(android.net.Uri.parse(baiGiang.getVideoUrl()), "video/*");
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Bài giảng này không có video.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayAudio(BaiGiang baiGiang) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && !baiGiangList.isEmpty()) {
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshData() {
        applyFilters();
    }

    public void resetFilters() {
        selectedCapDoHSKPosition = 0;
        selectedChuDePosition = 0;

        if (spinnerCapDoHSK != null) {
            spinnerCapDoHSK.setSelection(0);
        }

        if (spinnerChuDe != null) {
            spinnerChuDe.setSelection(0);
        }

        loadBaiGiangs(null, null, null, null);
    }
}