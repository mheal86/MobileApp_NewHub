package com.example.mobileapp_newhub.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.auth.AuthViewModel;
import com.example.mobileapp_newhub.auth.WelcomeActivity;

public class AdminActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_main, new AdminHomeFragment())
                    .commit();
        }

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(AuthViewModel.class);


    }
}