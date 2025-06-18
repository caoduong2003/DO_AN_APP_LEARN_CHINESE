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

public class StudentBaiGiangListFragment extends Fragment implements BaiGiangAdapter.OnBaiGiangItemClickListener {

    // UI Components
    private Spinner spinnerCapDoHSK, spinnerChuDe;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState, tvResultCount;
    private ChipGroup chipGroupViewMode, chipGroupFilter;
    private Chip chipListView, chipGridView;
    private Chip chipAll, chipVideo, chipPremium;

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
    private int currentContentFilter = 0; // 0: All, 1: Video only, 2: Premium only

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_bai_giang_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        setupChips();

        // Load data
        loadSpinnerData();
        loadBaiGiangs(null, null); // Load all initially

        return view;
    }

    private void initViews(View view) {
        spinnerCapDoHSK = view.findViewById(R.id.spinnerCapDoHSK);
        spinnerChuDe = view.findViewById(R.id.spinnerChuDe);
        rvBaiGiang = view.findViewById(R.id.rvBaiGiang);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvResultCount = view.findViewById(R.id.tvResultCount);

        // View mode chips
        chipGroupViewMode = view.findViewById(R.id.chipGroupViewMode);
        chipListView = view.findViewById(R.id.chipListView);
        chipGridView = view.findViewById(R.id.chipGridView);

        // Content filter chips
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        chipAll = view.findViewById(R.id.chipAll);
        chipVideo = view.findViewById(R.id.chipVideo);
        chipPremium = view.findViewById(R.id.chipPremium);

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(sessionManager);

        baiGiangList = new ArrayList<>();
        filteredList = new ArrayList<>();
        capDoHSKList = new ArrayList<>();
        chuDeList = new ArrayList<>();

        Log.d("STUDENT_FRAGMENT", "Fragment initialized");
    }

    private void setupRecyclerView() {
        // QUAN TRỌNG: Set isTeacher = false cho học sinh và sử dụng interface đúng
        adapter = new BaiGiangAdapter(requireContext(), filteredList, false, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);

        Log.d("STUDENT_FRAGMENT", "RecyclerView setup completed for student");
    }

    private void setupChips() {
        // Setup view mode chips
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

        // Setup content filter chips
        if (chipGroupFilter != null) {
            chipAll.setOnClickListener(v -> applyContentFilter(0));
            chipVideo.setOnClickListener(v -> applyContentFilter(1));
            chipPremium.setOnClickListener(v -> applyContentFilter(2));

            // Set default selection
            chipAll.setChecked(true);
        }
    }

    private void toggleViewMode() {
        isGridView = !isGridView;

        if (isGridView) {
            rvBaiGiang.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            if (chipGridView != null) chipGridView.setChecked(true);
            if (chipListView != null) chipListView.setChecked(false);
            Toast.makeText(requireContext(), "Chế độ lưới", Toast.LENGTH_SHORT).show();
        } else {
            rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
            if (chipListView != null) chipListView.setChecked(true);
            if (chipGridView != null) chipGridView.setChecked(false);
            Toast.makeText(requireContext(), "Chế độ danh sách", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
        Log.d("STUDENT_FRAGMENT", "View mode toggled to: " + (isGridView ? "Grid" : "List"));
    }

    private void applyContentFilter(int filterType) {
        currentContentFilter = filterType;
        filterCurrentList();

        String filterName = "";
        switch (filterType) {
            case 0: filterName = "Tất cả"; break;
            case 1: filterName = "Có video"; break;
            case 2: filterName = "Premium"; break;
        }
        Log.d("STUDENT_FRAGMENT", "Content filter applied: " + filterName);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            Log.d("STUDENT_FRAGMENT", "Refresh triggered");
            applyFilters();
        });

        // Spinner listeners
        spinnerCapDoHSK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedCapDoHSKPosition != position) {
                    selectedCapDoHSKPosition = position;
                    Log.d("STUDENT_FRAGMENT", "CapDoHSK filter changed to position: " + position);
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
                    Log.d("STUDENT_FRAGMENT", "ChuDe filter changed to position: " + position);
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
        Log.d("STUDENT_FRAGMENT", "Loading spinner data");

        // Load CapDoHSK data
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                Log.d("STUDENT_FRAGMENT", "Loaded " + capDoHSKs.size() + " CapDoHSK items");
                capDoHSKList = capDoHSKs;
                setupCapDoHSKSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("STUDENT_FRAGMENT", "Error loading CapDoHSK: " + errorMessage);
                Toast.makeText(requireContext(), "Lỗi tải cấp độ HSK: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Load ChuDe data
        baiGiangRepository.getAllChuDe(new BaiGiangRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDes) {
                Log.d("STUDENT_FRAGMENT", "Loaded " + chuDes.size() + " ChuDe items");
                chuDeList = chuDes;
                setupChuDeSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("STUDENT_FRAGMENT", "Error loading ChuDe: " + errorMessage);
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
        Log.d("STUDENT_FRAGMENT", "CapDoHSK spinner setup completed with " + spinnerItems.size() + " items");
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
        Log.d("STUDENT_FRAGMENT", "ChuDe spinner setup completed with " + spinnerItems.size() + " items");
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

        Log.d("STUDENT_FRAGMENT", "Applying filters - CapDoHSK_ID: " + capDoHSK_ID + ", ChuDeId: " + chuDeId);

        // Load bai giang with filters
        loadBaiGiangs(capDoHSK_ID, chuDeId);
    }

    private void loadBaiGiangs(Integer capDoHSK_ID, Integer chuDeId) {
        swipeRefresh.setRefreshing(true);
        showEmptyState(false);

        // QUAN TRỌNG: Chỉ lấy bài giảng đã được publish (trangThai = true) cho học sinh
        Boolean published = true;

        Log.d("STUDENT_FRAGMENT", "Loading published bài giảng for students - CapDoHSK: " + capDoHSK_ID + ", ChuDe: " + chuDeId);

        baiGiangRepository.getAllBaiGiang(null, null, capDoHSK_ID, chuDeId, published, new BaiGiangRepository.OnBaiGiangListCallback() {
            @Override
            public void onSuccess(List<BaiGiang> baiGiangs) {
                Log.d("STUDENT_FRAGMENT", "Successfully loaded " + baiGiangs.size() + " published bài giảng");

                baiGiangList = baiGiangs;
                filterCurrentList();
                swipeRefresh.setRefreshing(false);

                // Show empty state if no results after filtering
                if (filteredList.isEmpty()) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("STUDENT_FRAGMENT", "Error loading bài giảng: " + errorMessage);
                Toast.makeText(requireContext(), "Lỗi tải dữ liệu: " + errorMessage, Toast.LENGTH_LONG).show();
                swipeRefresh.setRefreshing(false);
                showEmptyState(true);
            }
        });
    }

    private void filterCurrentList() {
        filteredList.clear();

        for (BaiGiang baiGiang : baiGiangList) {
            boolean includeItem = true;

            // Apply content filter
            switch (currentContentFilter) {
                case 1: // Video only
                    includeItem = baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty();
                    break;
                case 2: // Premium only
                    includeItem = baiGiang.isLaBaiGiangGoi();
                    break;
                default: // All
                    includeItem = true;
                    break;
            }

            if (includeItem) {
                filteredList.add(baiGiang);
            }
        }

        adapter.updateData(filteredList);
        updateResultCount();

        Log.d("STUDENT_FRAGMENT", "Filtered list - Original: " + baiGiangList.size() + ", Filtered: " + filteredList.size());
    }

    private void updateResultCount() {
        if (tvResultCount != null) {
            int count = filteredList.size();
            String text = count + " bài giảng";

            // Add filter info
            if (selectedCapDoHSKPosition > 0 || selectedChuDePosition > 0 || currentContentFilter > 0) {
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
                String message;
                if (selectedCapDoHSKPosition > 0 || selectedChuDePosition > 0 || currentContentFilter > 0) {
                    message = "Không tìm thấy bài giảng phù hợp.\nThử thay đổi bộ lọc để xem thêm bài giảng.";
                } else {
                    message = "Chưa có bài giảng nào được phát hành.\nHãy quay lại sau!";
                }
                tvEmptyState.setText(message);
            }
        }

        rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);

        if (tvResultCount != null) {
            tvResultCount.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // QUAN TRỌNG: Implement interface OnBaiGiangItemClickListener đúng
    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d("STUDENT_FRAGMENT", "Student clicked on bài giảng: " + baiGiang.getTieuDe() + " (ID: " + baiGiang.getID() + ")");

        // Navigate to lesson detail screen - ĐÂY LÀ ĐIỂM QUAN TRỌNG
        Intent intent = new Intent(requireContext(), BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Không sử dụng cho học sinh - chỉ dành cho giáo viên
        Log.d("STUDENT_FRAGMENT", "Edit click not available for students");
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Không sử dụng cho học sinh - chỉ dành cho giáo viên
        Log.d("STUDENT_FRAGMENT", "Delete click not available for students");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to fragment
        if (adapter != null && !baiGiangList.isEmpty()) {
            filterCurrentList();
        }
    }

    // Public methods để control từ bên ngoài
    public void refreshData() {
        applyFilters();
    }

    public void resetFilters() {
        selectedCapDoHSKPosition = 0;
        selectedChuDePosition = 0;
        currentContentFilter = 0;

        if (spinnerCapDoHSK != null) {
            spinnerCapDoHSK.setSelection(0);
        }

        if (spinnerChuDe != null) {
            spinnerChuDe.setSelection(0);
        }

        if (chipAll != null) {
            chipAll.setChecked(true);
        }

        loadBaiGiangs(null, null);
    }

    // Public method để search từ bên ngoài (nếu cần)
    public void searchLessons(String query) {
        // Implementation for search if needed
        Log.d("STUDENT_FRAGMENT", "Search functionality can be implemented here: " + query);
    }
}