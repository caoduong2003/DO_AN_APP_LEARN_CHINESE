package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword, etFullName, etPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private AuthController authController;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();

        authController = new AuthController(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.setCancelable(false);
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            finish(); // Return to login screen
        });
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        progressDialog.show();

        authController.register(username, email, password, confirmPassword, fullName, phone,
                new AuthController.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Show success dialog
                        showSuccessDialog();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Đăng ký thành công");
        builder.setMessage("Bạn đã đăng ký tài khoản thành công. Vui lòng đăng nhập để tiếp tục.");
        builder.setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
            dialog.dismiss();
            finish(); // Return to login screen
        });
        builder.setCancelable(false);
        builder.show();
    }
}