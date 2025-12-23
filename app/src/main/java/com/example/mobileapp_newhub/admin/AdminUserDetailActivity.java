package com.example.mobileapp_newhub.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;

public class AdminUserDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        ImageView imgAvatar = findViewById(R.id.imgDetailAvatar);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvId = findViewById(R.id.tvDetailId);
        TextView tvRole = findViewById(R.id.tvDetailRole);
        Button btnClose = findViewById(R.id.btnClose);

        // Receive data from Intent
        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String role = getIntent().getStringExtra("role");
        String photoUrl = getIntent().getStringExtra("photoUrl");

        // Set data to views
        tvName.setText(name != null ? name : "No Name");
        tvEmail.setText(email != null ? email : "");
        tvId.setText(id != null ? id : "");
        tvRole.setText(role != null ? role : "user");

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).circleCrop().into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }

        btnClose.setOnClickListener(v -> finish());
    }
}
