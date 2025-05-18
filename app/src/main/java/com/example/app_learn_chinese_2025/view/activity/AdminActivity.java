package com.example.app_learn_chinese_2025.view.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.SessionManager;

public class AdminActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sessionManager = new SessionManager(this);
        User user = sessionManager.getUserDetails();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Xin chào " + user.getHoTen() + " (Admin)");
    }
}