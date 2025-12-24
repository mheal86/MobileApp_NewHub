package com.example.mobileapp_newhub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp_newhub.MainActivity;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.utils.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        checkAutoLogin();

        View btnAdmin = findViewById(R.id.btnAdmin);
        View btnUser = findViewById(R.id.btnUser);
        View btnGuest = findViewById(R.id.btnGuest);

        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> handleLoginClick("admin"));
        }
        
        if (btnUser != null) {
            btnUser.setOnClickListener(v -> handleLoginClick("user"));
        }

        if (btnGuest != null) {
            btnGuest.setOnClickListener(v -> {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("role", "guest"); // Đánh dấu là guest
                startActivity(intent);
            });
        }
    }

    private void handleLoginClick(String role) {
        // Nếu không có mạng và đang có user đăng nhập -> Cần logout trước khi vào LoginActivity
        if (!NetworkUtils.isNetworkAvailable(this) && FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            
            try {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut();
            } catch (Exception e) {
                // Ignore error
            }
        }

        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    private void checkAutoLogin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean isConnected = NetworkUtils.isNetworkAvailable(this);

        if (currentUser != null) {
            if (isConnected) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Không có kết nối mạng. Vui lòng kết nối để truy cập đầy đủ tính năng.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
