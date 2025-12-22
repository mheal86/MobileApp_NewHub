package com.example.mobileapp_newhub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Kết nối BottomNavigationView với NavController
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            
            // Xử lý chuyển hướng từ LoginActivity
            if (getIntent() != null && getIntent().getBooleanExtra("navigate_to_profile", false)) {
                navController.navigate(R.id.settingsFragment); // Tạm thời chuyển đến settings hoặc profile nếu có ID đúng
                // Hoặc nếu bạn muốn chuyển đến tab Profile (item menu id: nav_profile -> fragment id: nav_profile?)
                // BottomNavigationView id là nav_profile, thì graph id cũng phải là nav_profile để auto navigate.
                // Nếu không trùng, ta phải navigate thủ công:
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        }
    }
}
