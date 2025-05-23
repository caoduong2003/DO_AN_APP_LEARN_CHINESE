package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.repository.TuVungRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.TuVungAdapter;

import java.util.ArrayList;
import java.util.List;

public class TuVungListFragment extends Fragment {
    private EditText etSearch;
    private RecyclerView rvTuVung;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private TuVungRepository tuVungRepository;
    private TuVungAdapter adapter;

    private long baiGiangId;
    private List<TuVung> tuVungList;
    private List<TuVung> filteredList;

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
        View view = inflater.inflate(R.layout.fragment_tu_vung_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        // Load data
        loadTuVungList();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        rvTuVung = view.findViewById(R.id.rvTuVung);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(requireContext());
        tuVungRepository = new TuVungRepository(sessionManager);

        tuVungList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new TuVungAdapter(requireContext(), filteredList, false, null);
        rvTuVung.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTuVung.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadTuVungList);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTuVung(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void loadTuVungList() {
        if (baiGiangId <= 0) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }

        swipeRefresh.setRefreshing(true);

        tuVungRepository.getTuVungByBaiGiang(baiGiangId, new TuVungRepository.OnTuVungListCallback() {
            @Override
            public void onSuccess(List<TuVung> tuVungs) {
                tuVungList = tuVungs;
                filteredList = new ArrayList<>(tuVungs);
                adapter.updateData(filteredList);
                swipeRefresh.setRefreshing(false);

                // Apply current filter
                String currentFilter = etSearch.getText().toString().trim();
                if (!currentFilter.isEmpty()) {
                    filterTuVung(currentFilter);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void filterTuVung(String keyword) {
        if (keyword.isEmpty()) {
            // No filter, use all data
            filteredList = new ArrayList<>(tuVungList);
        } else {
            // Apply filter
            filteredList = new ArrayList<>();

            for (TuVung tuVung : tuVungList) {
                // Search in both Chinese and Vietnamese
                if (tuVung.getTiengTrung().toLowerCase().contains(keyword.toLowerCase()) ||
                        tuVung.getTiengViet().toLowerCase().contains(keyword.toLowerCase()) ||
                        (tuVung.getPhienAm() != null && tuVung.getPhienAm().toLowerCase().contains(keyword.toLowerCase()))) {
                    filteredList.add(tuVung);
                }
            }
        }

        adapter.updateData(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.release(); // Release MediaPlayer resources
        }
    }
}