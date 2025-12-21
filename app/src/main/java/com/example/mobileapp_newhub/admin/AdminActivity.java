package com.example.mobileapp_newhub.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.auth.LoginActivity;
import com.example.mobileapp_newhub.auth.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnManagePosts = findViewById(R.id.btnManagePosts);
        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnStatistics = findViewById(R.id.btnStatistics);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnManagePosts.setOnClickListener(v -> {
            // Sẽ chuyển đến màn quản lý bài viết sau (ví dụ: AddEditPostActivity)
            startActivity(new Intent(this, AddEditPostActivity.class)); 
        });

        btnManageUsers.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng quản lý người dùng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnStatistics.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminActivity.this, WelcomeActivity.class);
            // Xóa hết stack cũ để quay về màn hình Welcome sạch sẽ
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}