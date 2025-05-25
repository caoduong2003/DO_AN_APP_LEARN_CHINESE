package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.UserManagementRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.example.app_learn_chinese_2025.util.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditUserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvTitle, tvHSKLabel;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword, etFullName, etPhone;
    private RadioGroup rgRole;
    private RadioButton rbStudent, rbTeacher;
    private Spinner spinnerHSKLevel;
    private Button btnSave, btnCancel;

    private SessionManager sessionManager;
    private UserManagementRepository userRepository;
    private ProgressDialog progressDialog;

    private long userId = -1; // -1 for adding new, otherwise for editing
    private int userType = Constants.ROLE_STUDENT;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        // Get data from intent
        if (getIntent().hasExtra("USER_ID")) {
            userId = getIntent().getLongExtra("USER_ID", -1);
        }
        if (getIntent().hasExtra("USER_TYPE")) {
            userType = getIntent().getIntExtra("USER_TYPE", Constants.ROLE_STUDENT);
        }

        initViews();
        setupToolbar();
        setupListeners();
        setupHSKSpinner();

        if (userId != -1) {
            // Edit mode
            tvTitle.setText("Chỉnh sửa người dùng");
            loadUser(userId);
        } else {
            // Add mode
            tvTitle.setText("Thêm người dùng mới");
            setupDefaultRole();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvHSKLabel = findViewById(R.id.tvHSKLabel);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        rgRole = findViewById(R.id.rgRole);
        rbStudent = findViewById(R.id.rbStudent);
        rbTeacher = findViewById(R.id.rbTeacher);
        spinnerHSKLevel = findViewById(R.id.spinnerHSKLevel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        sessionManager = new SessionManager(this);
        userRepository = new UserManagementRepository(sessionManager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> finish());

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStudent) {
                showHSKLevel(true);
            } else {
                showHSKLevel(false);
            }
        });
    }

    private void setupHSKSpinner() {
        String[] hskLevels = {"Chưa xác định", "HSK 1", "HSK 2", "HSK 3", "HSK 4", "HSK 5", "HSK 6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hskLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHSKLevel.setAdapter(adapter);
    }

    private void setupDefaultRole() {
        if (userType == Constants.ROLE_TEACHER) {
            rbTeacher.setChecked(true);
            showHSKLevel(false);
        } else {
            rbStudent.setChecked(true);
            showHSKLevel(true);
        }
    }

    private void showHSKLevel(boolean show) {
        if (show) {
            tvHSKLabel.setVisibility(View.VISIBLE);
            spinnerHSKLevel.setVisibility(View.VISIBLE);
        } else {
            tvHSKLabel.setVisibility(View.GONE);
            spinnerHSKLevel.setVisibility(View.GONE);
        }
    }

    private void loadUser(long id) {
        progressDialog.show();

        userRepository.getUserById(id, new UserManagementRepository.OnUserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                populateFields(user);
                progressDialog.dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(AddEditUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(User user) {
        etUsername.setText(user.getTenDangNhap());
        etEmail.setText(user.getEmail());
        etFullName.setText(user.getHoTen());
        etPhone.setText(user.getSoDienThoai());

        // Set role
        if (user.getVaiTro() == Constants.ROLE_TEACHER) {
            rbTeacher.setChecked(true);
            showHSKLevel(false);
        } else {
            rbStudent.setChecked(true);
            showHSKLevel(true);

            // Set HSK level
            if (user.getTrinhDoHSK() >= 0 && user.getTrinhDoHSK() <= 6) {
                spinnerHSKLevel.setSelection(user.getTrinhDoHSK());
            }
        }

        // Hide password fields in edit mode
        etPassword.setVisibility(View.GONE);
        etConfirmPassword.setVisibility(View.GONE);
    }

    private void saveUser() {
        // Validate input
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!validateInput(username, email, password, confirmPassword, fullName, phone)) {
            return;
        }

        // Create or update user object
        User user;
        if (userId != -1 && currentUser != null) {
            // Edit mode
            user = currentUser;
        } else {
            // Add mode
            user = new User();
        }

        // Set basic fields
        user.setTenDangNhap(username);
        user.setEmail(email);
        user.setHoTen(fullName);
        user.setSoDienThoai(phone);

        // Set role
        if (rbTeacher.isChecked()) {
            user.setVaiTro(Constants.ROLE_TEACHER);
            user.setTrinhDoHSK(0); // Teachers don't have HSK level
        } else {
            user.setVaiTro(Constants.ROLE_STUDENT);
            user.setTrinhDoHSK(spinnerHSKLevel.getSelectedItemPosition());
        }

        // Set password only for new users
        if (userId == -1) {
            user.setMatKhau(password); // This will be hashed on the server
        }

        user.setTrangThai(true); // Default to active

        progressDialog.show();

        if (userId != -1) {
            // Update existing user
            userRepository.updateUser(userId, user, new UserManagementRepository.OnUserCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(AddEditUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new user
            userRepository.createUser(user, new UserManagementRepository.OnUserCallback() {
                @Override
                public void onSuccess(User newUser) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(AddEditUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInput(String username, String email, String password,
                                  String confirmPassword, String fullName, String phone) {
        if (!ValidationUtils.isValidUsername(username)) {
            etUsername.setError("Tên đăng nhập phải có ít nhất 4 ký tự và không chứa khoảng trắng");
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Email không hợp lệ");
            return false;
        }

        // Only validate password for new users
        if (userId == -1) {
            if (!ValidationUtils.isValidPassword(password)) {
                etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return false;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu nhập lại không khớp");
                return false;
            }
        }

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            etPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }

        return true;
    }
}