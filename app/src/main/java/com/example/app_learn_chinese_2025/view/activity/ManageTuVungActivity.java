package com.example.app_learn_chinese_2025.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.model.repository.TuVungRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.TuVungAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTuVungActivity extends AppCompatActivity implements TuVungAdapter.OnTuVungActionListener {
    private static final int REQUEST_ADD_TU_VUNG = 1001;
    private static final int REQUEST_EDIT_TU_VUNG = 1002;

    private TextView tvTitle;
    private Button btnAddTuVung;
    private RecyclerView rvTuVung;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private TuVungRepository tuVungRepository;
    private TuVungAdapter adapter;

    private long baiGiangId;
    private BaiGiang currentBaiGiang;
    private List<TuVung> tuVungList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tu_vung);

        // Get baiGiangId from intent extras (required)
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        setupRecyclerView();

        // Load data
        loadBaiGiang();
        loadTuVungList();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        btnAddTuVung = findViewById(R.id.btnAddTuVung);
        rvTuVung = findViewById(R.id.rvTuVung);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(this);
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        tuVungRepository = new TuVungRepository(sessionManager);
        tuVungList = new ArrayList<>();
    }

    private void setupListeners() {
        btnAddTuVung.setOnClickListener(v -> {
            Intent intent = new Intent(ManageTuVungActivity.this, EditTuVungActivity.class);
            intent.putExtra("BAI_GIANG_ID", baiGiangId);
            startActivityForResult(intent, REQUEST_ADD_TU_VUNG);
        });

        swipeRefresh.setOnRefreshListener(this::loadTuVungList);
    }

    private void setupRecyclerView() {
        adapter = new TuVungAdapter(this, tuVungList, true, this);
        rvTuVung.setLayoutManager(new LinearLayoutManager(this));
        rvTuVung.setAdapter(adapter);
    }

    private void loadBaiGiang() {
        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;

                // Update title
                tvTitle.setText("Từ vựng: " + baiGiang.getTieuDe());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ManageTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTuVungList() {
        swipeRefresh.setRefreshing(true);

        tuVungRepository.getTuVungByBaiGiang(baiGiangId, new TuVungRepository.OnTuVungListCallback() {
            @Override
            public void onSuccess(List<TuVung> tuVungList) {
                ManageTuVungActivity.this.tuVungList = tuVungList;
                adapter.updateData(tuVungList);
                swipeRefresh.setRefreshing(false);

                // Show message if list is empty
                if (tuVungList.isEmpty()) {
                    Toast.makeText(ManageTuVungActivity.this, "Chưa có từ vựng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ManageTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onEditClick(TuVung tuVung) {
        Intent intent = new Intent(ManageTuVungActivity.this, EditTuVungActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiangId);
        intent.putExtra("TU_VUNG_ID", tuVung.getId());
        startActivityForResult(intent, REQUEST_EDIT_TU_VUNG);
    }

    @Override
    public void onDeleteClick(TuVung tuVung) {
        tuVungRepository.deleteTuVung(tuVung.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageTuVungActivity.this, "Xóa từ vựng thành công", Toast.LENGTH_SHORT).show();
                    loadTuVungList(); // Reload the list
                } else {
                    Toast.makeText(ManageTuVungActivity.this, "Xóa từ vựng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageTuVungActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_TU_VUNG || requestCode == REQUEST_EDIT_TU_VUNG) {
                loadTuVungList(); // Reload the list after add/edit
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.release(); // Release MediaPlayer resources
        }
    }
}