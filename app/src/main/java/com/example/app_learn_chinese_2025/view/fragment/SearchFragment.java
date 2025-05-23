package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.repository.TuVungRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.TuVungAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText etSearch;
    private RadioGroup rgSearchType;
    private RadioButton rbTiengTrung, rbTiengViet;
    private RecyclerView rvSearchResults;

    private SessionManager sessionManager;
    private TuVungRepository tuVungRepository;
    private TuVungAdapter adapter;
    private List<TuVung> searchResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        rgSearchType = view.findViewById(R.id.rgSearchType);
        rbTiengTrung = view.findViewById(R.id.rbTiengTrung);
        rbTiengViet = view.findViewById(R.id.rbTiengViet);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        sessionManager = new SessionManager(requireContext());
        tuVungRepository = new TuVungRepository(sessionManager);
        searchResults = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new TuVungAdapter(requireContext(), searchResults, false, null);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSearchResults.setAdapter(adapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search when text changes
                if (s.length() >= 2) { // Search only if at least 2 characters
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    // Clear results when search box is empty
                    searchResults.clear();
                    adapter.updateData(searchResults);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        // Search type listener
        rgSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            String currentSearch = etSearch.getText().toString().trim();
            if (currentSearch.length() >= 2) {
                performSearch(currentSearch);
            }
        });
    }

    private void performSearch(String keyword) {
        // Determine search language
        String language = rbTiengViet.isChecked() ? "vi" : "zh";

        tuVungRepository.searchTuVung(keyword, language, new TuVungRepository.OnTuVungListCallback() {
            @Override
            public void onSuccess(List<TuVung> tuVungList) {
                searchResults = tuVungList;
                adapter.updateData(tuVungList);

                if (tuVungList.isEmpty()) {
                    Toast.makeText(requireContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.release(); // Release MediaPlayer resources
        }
    }
}