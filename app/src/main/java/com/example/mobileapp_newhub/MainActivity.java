package com.example.mobileapp_newhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileapp_newhub.auth.LoginActivity;
import com.example.mobileapp_newhub.ui.bookmark.BookmarkFragment;
import com.example.mobileapp_newhub.ui.category.CategoryFragment;
import com.example.mobileapp_newhub.ui.home.HomeFragment;
import com.example.mobileapp_newhub.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Mặc định load Trang chủ (Home) khi vừa vào
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment fragment = null;
                        int id = item.getItemId();

                        if (id == R.id.nav_home) {
                            fragment = new HomeFragment();
                        } else if (id == R.id.nav_category) {
                            fragment = new CategoryFragment();
                        } else if (id == R.id.nav_bookmark) {
                            fragment = new BookmarkFragment();
                        } else if (id == R.id.nav_profile) {
                            fragment = new ProfileFragment();
                        }

                        if (fragment != null) {
                            loadFragment(fragment);
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_main, fragment)
                .commit();
    }
}