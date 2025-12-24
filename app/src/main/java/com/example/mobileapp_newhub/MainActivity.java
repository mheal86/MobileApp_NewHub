package com.example.mobileapp_newhub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mobileapp_newhub.auth.AuthViewModel;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AuthViewModel authViewModel;
    private ReaderViewModel readerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModels
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        readerViewModel = new ViewModelProvider(this).get(ReaderViewModel.class);

        // Setup observer for user authentication state
        setupAuthObserver();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Connect BottomNavigationView with NavController
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            // Handle navigation from LoginActivity
            if (getIntent() != null && getIntent().getBooleanExtra("navigate_to_profile", false)) {
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        }
    }

    private void setupAuthObserver() {
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // User has logged in, load their data
                readerViewModel.loadSavedPosts();
                readerViewModel.loadHistoryPosts();
            } else {
                // User has logged out, clear all user-specific data
                readerViewModel.clearUserData();
            }
        });
    }
}
