package com.example.mobileapp_newhub;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

// import com.example.mobileapp_newhub.ui.WelcomeActivity; // Xóa import sai
import com.example.mobileapp_newhub.auth.WelcomeActivity; // Import đúng
import com.example.mobileapp_newhub.utils.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            
            if (getIntent() != null && getIntent().getBooleanExtra("navigate_to_profile", false)) {
                navController.navigate(R.id.settingsFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkAndAuth();
    }
    
    private void checkNetworkAndAuth() {
        // Nếu không có mạng
        if (!NetworkUtils.isNetworkAvailable(this)) {
            // Kiểm tra trạng thái đăng nhập
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // ĐÃ ĐĂNG NHẬP (User/Admin) + MẤT MẠNG -> Không cho dùng -> Về Welcome
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // CHƯA ĐĂNG NHẬP (Guest) + MẤT MẠNG -> Cho phép dùng tiếp (để xem cache)
                // Không làm gì cả
            }
        }
    }
}
