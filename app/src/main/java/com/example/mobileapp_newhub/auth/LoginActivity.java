package com.example.mobileapp_newhub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp_newhub.MainActivity;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.AdminActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText emailEditText, passwordEditText;
    private TextView tvLoginTitle, forgotPasswordTextView;
    private Button googleSignInButton;
    private LinearLayout layoutRegister;
    private GoogleSignInClient mGoogleSignInClient;
    private String userRole;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Lấy vai trò (user/admin)
        userRole = getIntent().getStringExtra("role");
        if (userRole == null) userRole = "user";

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Ánh xạ View
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        layoutRegister = findViewById(R.id.layoutRegister);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        TextView registerTextView = findViewById(R.id.registerTextView);

        // --- CẤU HÌNH GIAO DIỆN THEO VAI TRÒ ---
        if ("admin".equals(userRole)) {
            // Chế độ Admin
            tvLoginTitle.setText("Đăng nhập Quản trị viên");
            
            emailEditText.setHint("ID Tài khoản");
            emailEditText.setInputType(InputType.TYPE_CLASS_TEXT); // Cho phép nhập text thường (không bắt buộc @)

            googleSignInButton.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.GONE);
            forgotPasswordTextView.setVisibility(View.GONE); // Admin thường không tự reset pass qua email
        } else {
            // Chế độ User (Mặc định)
            tvLoginTitle.setText("Đăng nhập");
            
            emailEditText.setHint("Email");
            emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            googleSignInButton.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.VISIBLE);
            forgotPasswordTextView.setVisibility(View.VISIBLE);
        }

        // Cấu hình Google Sign In (chỉ dùng cho User, nhưng khởi tạo cũng không sao)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(v -> {
            String emailOrId = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (emailOrId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu là Admin và ID không phải email, có thể cần xử lý thêm (ví dụ thêm @admin.com)
            // Ở đây tạm thời gửi nguyên chuỗi vào authViewModel
            authViewModel.login(emailOrId, password);
        });

        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });

        // Quan sát kết quả Login
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent;
                if ("admin".equals(userRole)) {
                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        
        authViewModel.getPasswordResetLiveData().observe(this, success -> {
            if (success != null && success) {
                 Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authViewModel.firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showForgotPasswordDialog() {
        EditText resetEmail = new EditText(this);
        resetEmail.setHint("Enter your email");
        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive reset link.")
                .setView(resetEmail)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = resetEmail.getText().toString();
                    if (!email.isEmpty()) {
                        authViewModel.resetPassword(email);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}