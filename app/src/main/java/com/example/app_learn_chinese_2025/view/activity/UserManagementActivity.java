package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.UserManagementRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.view.adapter.UserManagementAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity implements UserManagementAdapter.OnUserActionListener {
    private static final int REQUEST_ADD_USER = 1001;
    private static final int REQUEST_EDIT_USER = 1002;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private EditText etSearch;
    private RecyclerView rvUsers;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddUser;
    private TextView tvEmptyMessage;

    private SessionManager sessionManager;
    private UserManagementRepository userRepository;
    private UserManagementAdapter adapter;

    private List<User> allUsers;
    private List<User> filteredUsers;
    private int currentUserType = Constants.ROLE_STUDENT; // Default to students

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();
        setupListeners();

        // Load initial data
        loadUsers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        etSearch = findViewById(R.id.etSearch);
        rvUsers = findViewById(R.id.rvUsers);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabAddUser = findViewById(R.id.fabAddUser);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        sessionManager = new SessionManager(this);
        userRepository = new UserManagementRepository(sessionManager);

        allUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý người dùng");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Học viên"));
        tabLayout.addTab(tabLayout.newTab().setText("Giáo viên"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentUserType = Constants.ROLE_STUDENT;
                        break;
                    case 1:
                        currentUserType = Constants.ROLE_TEACHER;
                        break;
                }
                loadUsers();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new UserManagementAdapter(this, filteredUsers, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadUsers);

        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, AddEditUserActivity.class);
            intent.putExtra("USER_TYPE", currentUserType);
            startActivityForResult(intent, REQUEST_ADD_USER);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUsers() {
        swipeRefresh.setRefreshing(true);

        userRepository.getUsersByRole(currentUserType, new UserManagementRepository.OnUserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers = users;
                filteredUsers = new ArrayList<>(users);
                adapter.updateData(filteredUsers);

                updateEmptyState();
                swipeRefresh.setRefreshing(false);

                // Apply current search filter
                String currentSearch = etSearch.getText().toString().trim();
                if (!currentSearch.isEmpty()) {
                    filterUsers(currentSearch);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(UserManagementActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
                updateEmptyState();
            }
        });
    }

    private void filterUsers(String keyword) {
        if (keyword.isEmpty()) {
            filteredUsers = new ArrayList<>(allUsers);
        } else {
            filteredUsers = new ArrayList<>();
            for (User user : allUsers) {
                if (user.getHoTen().toLowerCase().contains(keyword.toLowerCase()) ||
                        user.getTenDangNhap().toLowerCase().contains(keyword.toLowerCase()) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword.toLowerCase()))) {
                    filteredUsers.add(user);
                }
            }
        }

        adapter.updateData(filteredUsers);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredUsers.isEmpty()) {
            rvUsers.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);

            String userTypeText = currentUserType == Constants.ROLE_STUDENT ? "học viên" : "giáo viên";
            tvEmptyMessage.setText("Chưa có " + userTypeText + " nào");
        } else {
            rvUsers.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditUser(User user) {
        Intent intent = new Intent(UserManagementActivity.this, AddEditUserActivity.class);
        intent.putExtra("USER_ID", user.getID());
        intent.putExtra("USER_TYPE", user.getVaiTro());
        startActivityForResult(intent, REQUEST_EDIT_USER);
    }

    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng \"" + user.getHoTen() + "\"?\n\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onToggleStatus(User user) {
        String action = user.getTrangThai() ? "khóa" : "kích hoạt";
        String message = "Bạn có chắc chắn muốn " + action + " tài khoản \"" + user.getHoTen() + "\"?";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận " + action)
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> toggleUserStatus(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(User user) {
        userRepository.deleteUser(user.getID(), new UserManagementRepository.OnUserCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(UserManagementActivity.this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                loadUsers(); // Reload the list
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(UserManagementActivity.this, "Xóa người dùng thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleUserStatus(User user) {
        userRepository.toggleUserStatus(user.getID(), new UserManagementRepository.OnUserCallback() {
            @Override
            public void onSuccess(User updatedUser) {
                String status = updatedUser.getTrangThai() ? "kích hoạt" : "khóa";
                Toast.makeText(UserManagementActivity.this, "Đã " + status + " tài khoản thành công", Toast.LENGTH_SHORT).show();
                loadUsers(); // Reload the list
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(UserManagementActivity.this, "Cập nhật trạng thái thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_USER) {
                Toast.makeText(this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else if (requestCode == REQUEST_EDIT_USER) {
                Toast.makeText(this, "Cập nhật người dùng thành công", Toast.LENGTH_SHORT).show();
                loadUsers();
            }
        }
    }
}