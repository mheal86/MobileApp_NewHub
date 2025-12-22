package com.example.mobileapp_newhub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp_newhub.MainActivity;
import com.example.mobileapp_newhub.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Sử dụng View thay vì Button vì giao diện XML mới dùng MaterialCardView
        View btnAdmin = findViewById(R.id.btnAdmin);
        View btnUser = findViewById(R.id.btnUser);
        View btnGuest = findViewById(R.id.btnGuest);

        btnAdmin.setOnClickListener(v -> {
            // Chuyển sang màn hình Login, đánh dấu là Admin
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.putExtra("role", "admin");
            startActivity(intent);
        });

        btnUser.setOnClickListener(v -> {
            // Chuyển sang màn hình Login cho User
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.putExtra("role", "user");
            startActivity(intent);
        });

        btnGuest.setOnClickListener(v -> {
            // Vào thẳng App với chế độ Khách
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("role", "guest");
            startActivity(intent);
        });
    }
}