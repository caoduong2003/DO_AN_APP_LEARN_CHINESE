package com.example.app_learn_chinese_2025.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class StudentBaiGiangListFragment extends Fragment implements BaiGiangAdapter.OnBaiGiangActionListener {
    private Spinner spinnerCapDoHSK, spinnerChuDe;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private BaiGiangAdapter adapter;

    private List<BaiGiang> baiGiangList;
    private List<CapDoHSK> capDoHSKList;
    private List<ChuDe> chuDeList;

    private int selectedCapDoHSKPosition = 0;
    private int selectedChuDePosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bai_giang_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

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

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(sessionManager);

        baiGiangList = new ArrayList<>();
        capDoHSKList = new ArrayList<>();
        chuDeList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        // QUAN TRỌNG: Set isTeacher = false cho học sinh
        adapter = new BaiGiangAdapter(requireContext(), baiGiangList, false, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            // Get current filters
            Integer capDoHSK_ID = null;
            Integer chuDeId = null;

            if (selectedCapDoHSKPosition > 0 && !capDoHSKList.isEmpty()) {
                capDoHSK_ID = capDoHSKList.get(selectedCapDoHSKPosition - 1).getID(); // -1 because first item is "Tất cả"
            }

            if (selectedChuDePosition > 0 && !chuDeList.isEmpty()) {
                chuDeId = chuDeList.get(selectedChuDePosition - 1).getID(); // -1 because first item is "Tất cả"
            }

            loadBaiGiangs(capDoHSK_ID, chuDeId);
        });

        // Spinner listeners
        spinnerCapDoHSK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCapDoHSKPosition = position;
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerChuDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChuDePosition = position;
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadSpinnerData() {
        // Load CapDoHSK data
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                capDoHSKList = capDoHSKs;
                setupCapDoHSKSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Load ChuDe data
        baiGiangRepository.getAllChuDe(new BaiGiangRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDes) {
                chuDeList = chuDes;
                setupChuDeSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
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
    }

    private void applyFilters() {
        // Get selected filters
        Integer capDoHSK_ID = null;
        Integer chuDeId = null;

        if (selectedCapDoHSKPosition > 0 && !capDoHSKList.isEmpty()) {
            capDoHSK_ID = capDoHSKList.get(selectedCapDoHSKPosition - 1).getID(); // -1 because first item is "Tất cả"
        }

        if (selectedChuDePosition > 0 && !chuDeList.isEmpty()) {
            chuDeId = chuDeList.get(selectedChuDePosition - 1).getID(); // -1 because first item is "Tất cả"
        }

        // Load bai giang with filters
        loadBaiGiangs(capDoHSK_ID, chuDeId);
    }

    private void loadBaiGiangs(Integer capDoHSK_ID, Integer chuDeId) {
        swipeRefresh.setRefreshing(true);

        // QUAN TRỌNG: Chỉ lấy bài giảng đã được publish (trangThai = true)
        Boolean published = true;

        baiGiangRepository.getAllBaiGiang(null, null, capDoHSK_ID, chuDeId, published, new BaiGiangRepository.OnBaiGiangListCallback() {
            @Override
            public void onSuccess(List<BaiGiang> baiGiangs) {
                baiGiangList = baiGiangs;
                adapter.updateData(baiGiangs);
                swipeRefresh.setRefreshing(false);

                // Show message if list is empty
                if (baiGiangs.isEmpty()) {
                    Toast.makeText(requireContext(), "Không có bài giảng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(BaiGiang baiGiang) {
        // Navigate to lesson detail screen - ĐÂY LÀ ĐIỂM QUAN TRỌNG
        Intent intent = new Intent(requireContext(), BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Không sử dụng cho học sinh
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Không sử dụng cho học sinh
    }
}