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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiTapController;
import com.example.app_learn_chinese_2025.model.data.BaiTap;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.KetQuaBaiTap;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.activity.QuizActivity;
import com.example.app_learn_chinese_2025.view.adapter.BaiTapAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentExerciseFragment extends Fragment implements
        BaiTapController.BaiTapControllerListener {

    private static final String TAG = "STUDENT_EXERCISE_FRAGMENT";

    // UI Components
    private RecyclerView rvBaiTap;
    private SwipeRefreshLayout swipeRefresh;
    private Spinner spinnerCapDoHSK, spinnerChuDe;
    private TextView tvEmptyState;

    // Data & Controllers
    private BaiTapController baiTapController;
    private BaiGiangRepository baiGiangRepository; // Use existing repository
    private SessionManager sessionManager;

    // Data lists
    private List<BaiTap> baiTapList;
    private List<CapDoHSK> capDoHSKList;
    private List<ChuDe> chuDeList;

    // Adapter
    private BaiTapAdapter adapter;

    // Filter states
    private int selectedCapDoHSKPosition = 0;
    private int selectedChuDePosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize controllers using existing pattern
        sessionManager = new SessionManager(requireContext());
        baiTapController = new BaiTapController(sessionManager);
        baiGiangRepository = new BaiGiangRepository(requireContext(),sessionManager); // Use existing repository

        // Set listeners
        baiTapController.setListener(this);

        // Initialize data lists
        baiTapList = new ArrayList<>();
        capDoHSKList = new ArrayList<>();
        chuDeList = new ArrayList<>();

        Log.d(TAG, "Fragment created");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_exercise, container, false);

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupSpinners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load initial data
        loadInitialData();
    }

    private void initViews(View view) {
        rvBaiTap = view.findViewById(R.id.rvBaiTap);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        spinnerCapDoHSK = view.findViewById(R.id.spinnerCapDoHSK);
        spinnerChuDe = view.findViewById(R.id.spinnerChuDe);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        Log.d(TAG, "Views initialized");
    }

    private void setupRecyclerView() {
        adapter = new BaiTapAdapter(requireContext(), baiTapList);
        adapter.setOnItemClickListener(new BaiTapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaiTap baiTap) {
                openQuizActivity(baiTap);
            }
        });

        rvBaiTap.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiTap.setAdapter(adapter);

        Log.d(TAG, "RecyclerView setup completed");
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadBaiTapList);
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupSpinners() {
        // Setup CapDoHSK Spinner
        spinnerCapDoHSK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedCapDoHSKPosition != position) {
                    selectedCapDoHSKPosition = position;
                    applyFilters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup ChuDe Spinner
        spinnerChuDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedChuDePosition != position) {
                    selectedChuDePosition = position;
                    applyFilters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Log.d(TAG, "Spinners setup completed");
    }

    private void loadInitialData() {
        Log.d(TAG, "Loading initial data...");

        // Test API first
        baiTapController.pingBaiTap();

        // Load filter data using existing BaiGiangRepository
        loadCapDoHSKData();
        loadChuDeData();

        // Load bai tap list
        loadBaiTapList();
    }

    private void loadCapDoHSKData() {
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                Log.d(TAG, "CapDoHSK list loaded: " + capDoHSKs.size() + " items");
                capDoHSKList.clear();
                capDoHSKList.addAll(capDoHSKs);
                setupCapDoHSKSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading CapDoHSK: " + errorMessage);
                showError("Không thể tải danh sách cấp độ HSK");
            }
        });
    }

    private void loadChuDeData() {
        baiGiangRepository.getAllChuDe(new BaiGiangRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDes) {
                Log.d(TAG, "ChuDe list loaded: " + chuDes.size() + " items");
                chuDeList.clear();
                chuDeList.addAll(chuDes);
                setupChuDeSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading ChuDe: " + errorMessage);
                showError("Không thể tải danh sách chủ đề");
            }
        });
    }

    private void loadBaiTapList() {
        swipeRefresh.setRefreshing(true);
        showEmptyState(false);

        Integer capDoHSKId = selectedCapDoHSKPosition > 0 && selectedCapDoHSKPosition <= capDoHSKList.size() ?
                capDoHSKList.get(selectedCapDoHSKPosition - 1).getID() : null;

        Integer chuDeId = selectedChuDePosition > 0 && selectedChuDePosition <= chuDeList.size() ?
                chuDeList.get(selectedChuDePosition - 1).getID() : null;

        Log.d(TAG, "Loading bai tap list with filters - CapDoHSK: " + capDoHSKId + ", ChuDe: " + chuDeId);

        baiTapController.getBaiTapList(capDoHSKId, chuDeId);
    }

    private void applyFilters() {
        Log.d(TAG, "Applying filters - CapDoHSK position: " + selectedCapDoHSKPosition +
                ", ChuDe position: " + selectedChuDePosition);
        loadBaiTapList();
    }

    private void openQuizActivity(BaiTap baiTap) {
        Log.d(TAG, "Opening quiz activity for bai tap: " + baiTap.getId());

        Intent intent = new Intent(requireContext(), QuizActivity.class);
        intent.putExtra("BAI_TAP_ID", baiTap.getId());
        intent.putExtra("BAI_TAP_TITLE", baiTap.getTieuDe());
        startActivity(intent);
    }

    private void showEmptyState(boolean show) {
        if (show) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvBaiTap.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvBaiTap.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setupCapDoHSKSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả cấp độ");

        for (CapDoHSK capDoHSK : capDoHSKList) {
            spinnerItems.add(capDoHSK.getTenCapDo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapDoHSK.setAdapter(adapter);

        spinnerCapDoHSK.setSelection(0);
        Log.d(TAG, "CapDoHSK spinner setup completed with " + spinnerItems.size() + " items");
    }

    private void setupChuDeSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả chủ đề");

        for (ChuDe chuDe : chuDeList) {
            spinnerItems.add(chuDe.getTenChuDe());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChuDe.setAdapter(adapter);

        spinnerChuDe.setSelection(0);
        Log.d(TAG, "ChuDe spinner setup completed with " + spinnerItems.size() + " items");
    }

    // BaiTapController.BaiTapControllerListener implementation
    @Override
    public void onBaiTapListLoaded(List<BaiTap> baiTapList) {
        Log.d(TAG, "Bai tap list loaded: " + baiTapList.size() + " items");

        swipeRefresh.setRefreshing(false);

        this.baiTapList.clear();
        this.baiTapList.addAll(baiTapList);
        adapter.notifyDataSetChanged();

        if (baiTapList.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }

    @Override
    public void onBaiTapDetailLoaded(BaiTap baiTap) {
        // Not used in this fragment
    }

    @Override
    public void onBaiTapSubmitted(KetQuaBaiTap ketQua) {
        // Not used in this fragment
    }

    @Override
    public void onKetQuaListLoaded(List<KetQuaBaiTap> ketQuaList) {
        // Not used in this fragment
    }

    @Override
    public void onPingSuccess(String message) {
        Log.d(TAG, "API Ping successful: " + message);
        if (getContext() != null) {
            Toast.makeText(requireContext(), "Kết nối API thành công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error from BaiTapController: " + error);
        swipeRefresh.setRefreshing(false);
        showError(error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clean up listeners
        if (baiTapController != null) {
            baiTapController.setListener(null);
        }

        Log.d(TAG, "Fragment destroyed");
    }
}