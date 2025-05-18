package com.example.app_learn_chinese_2025.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.AuthController;
import com.example.app_learn_chinese_2025.util.Constants;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Log;  // Thêm import Log
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AuthController authController;
    private ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity"; // Thêm TAG để log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();

        authController = new AuthController(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener((View v) -> attemptLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "Attempting login with username: " + username);
        progressDialog.show();

        authController.login(username, password, new AuthController.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                redirectBasedOnRole(authController.getUserRole());
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                String displayMessage = TextUtils.isEmpty(errorMessage) ? "Đã xảy ra lỗi không xác định" : errorMessage;
                Log.e(TAG, "Login error: " + displayMessage);
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Lỗi đăng nhập")
                        .setMessage(displayMessage)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }


    private void redirectBasedOnRole(int role) {
        Intent intent;

        switch (role) {
            case Constants.ROLE_ADMIN:
                intent = new Intent(LoginActivity.this, AdminActivity.class);
                break;
            case Constants.ROLE_TEACHER:
                intent = new Intent(LoginActivity.this, TeacherActivity.class);
                break;
            case Constants.ROLE_STUDENT:
                intent = new Intent(LoginActivity.this, StudentActivity.class);
                break;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish(); // Close login screen
    }
}