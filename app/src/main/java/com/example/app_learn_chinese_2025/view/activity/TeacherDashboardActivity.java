package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherDashboardActivity extends AppCompatActivity implements BaiGiangAdapter.OnBaiGiangActionListener {
    private static final int REQUEST_ADD_BAI_GIANG = 1001;
    private static final int REQUEST_EDIT_BAI_GIANG = 1002;

    private TextView tvWelcome;
    private Button btnLogout, btnAddBaiGiang;
    private RecyclerView rvBaiGiang;
    private SwipeRefreshLayout swipeRefresh;

    private SessionManager sessionManager;
    private AuthController authController;
    private BaiGiangRepository baiGiangRepository;
    private BaiGiangAdapter adapter;
    private List<BaiGiang> baiGiangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        initViews();
        setupListeners();
        setupRecyclerView();
        loadUserInfo();
        loadBaiGiangs();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddBaiGiang = findViewById(R.id.btnAddBaiGiang);
        rvBaiGiang = findViewById(R.id.rvBaiGiang);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        sessionManager = new SessionManager(this);
        authController = new AuthController(this);
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        baiGiangList = new ArrayList<>();
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());

        btnAddBaiGiang.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, EditBaiGiangActivity.class);
            startActivityForResult(intent, REQUEST_ADD_BAI_GIANG);
        });

        swipeRefresh.setOnRefreshListener(this::loadBaiGiangs);
    }

    private void setupRecyclerView() {
        adapter = new BaiGiangAdapter(this, baiGiangList, true, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(this));
        rvBaiGiang.setAdapter(adapter);
    }

    private void loadUserInfo() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            tvWelcome.setText("Xin chào " + user.getHoTen() + " (Giáo viên)");
        }
    }

    private void loadBaiGiangs() {
        swipeRefresh.setRefreshing(true);

        // Lấy ID của giáo viên đang đăng nhập
        User user = sessionManager.getUserDetails();
        if (user != null) {
            baiGiangRepository.getBaiGiangByGiangVien(user.getID(), new BaiGiangRepository.OnBaiGiangListCallback() {
                @Override
                public void onSuccess(List<BaiGiang> baiGiangList) {
                    TeacherDashboardActivity.this.baiGiangList = baiGiangList;
                    adapter.updateData(baiGiangList);
                    swipeRefresh.setRefreshing(false);

                    // Hiển thị thông báo nếu không có bài giảng
                    if (baiGiangList.isEmpty()) {
                        Toast.makeText(TeacherDashboardActivity.this, "Bạn chưa có bài giảng nào", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(TeacherDashboardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // Xử lý đăng xuất
        authController.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(TeacherDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(BaiGiang baiGiang) {
        // Chuyển đến màn hình chi tiết bài giảng
        Intent intent = new Intent(TeacherDashboardActivity.this, BaiGiangDetailActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivity(intent);
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Chuyển đến màn hình chỉnh sửa bài giảng
        Intent intent = new Intent(TeacherDashboardActivity.this, EditBaiGiangActivity.class);
        intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
        startActivityForResult(intent, REQUEST_EDIT_BAI_GIANG);
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Hiển thị dialog xác nhận xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bài giảng này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteBaiGiang(baiGiang))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteBaiGiang(BaiGiang baiGiang) {
        baiGiangRepository.deleteBaiGiang(baiGiang.getID(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeacherDashboardActivity.this, "Xóa bài giảng thành công", Toast.LENGTH_SHORT).show();
                    loadBaiGiangs(); // Tải lại danh sách
                } else {
                    Toast.makeText(TeacherDashboardActivity.this, "Xóa bài giảng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TeacherDashboardActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_BAI_GIANG) {
                loadBaiGiangs(); // Tải lại danh sách sau khi thêm
                Toast.makeText(this, "Thêm bài giảng thành công", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_EDIT_BAI_GIANG) {
                loadBaiGiangs(); // Tải lại danh sách sau khi sửa
                Toast.makeText(this, "Cập nhật bài giảng thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }
}