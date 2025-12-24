package com.example.mobileapp_newhub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.mobileapp_newhub.utils.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText emailEditText, passwordEditText;
    private TextView tvLoginTitle, forgotPasswordTextView;
    private Button googleSignInButton;
    private LinearLayout layoutRegister;
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

        userRole = getIntent().getStringExtra("role");
        if (userRole == null) userRole = "user";

        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(AuthViewModel.class);

        // Ánh xạ nút Back và xử lý chuyển hướng về WelcomeActivity
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            // Xóa hết Activity trước đó để đảm bảo người dùng bắt đầu lại từ Welcome
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        layoutRegister = findViewById(R.id.layoutRegister);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        TextView registerTextView = findViewById(R.id.registerTextView);

        setupUI();

        loginButton.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có kết nối mạng! Vui lòng kiểm tra lại.", Toast.LENGTH_LONG).show();
                return;
            }

            String input = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("admin".equals(userRole)) {
                if (!input.contains("@")) {
                    input += "@newhub.admin";
                }
            }
            authViewModel.login(input, password);
        });

        googleSignInButton.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent signInIntent = authViewModel.getGoogleSignInIntent();
            if (signInIntent != null) {
                googleSignInLauncher.launch(signInIntent);
            }
        });

        registerTextView.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        forgotPasswordTextView.setOnClickListener(v -> showForgotPasswordDialog());

        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                if (!NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "Mất mạng! Không thể đăng nhập.", Toast.LENGTH_LONG).show();
                    authViewModel.signOut();
                    return;
                }

                if ("admin".equals(userRole)) {
                    checkAdminRole(firebaseUser.getUid());
                } else {
                    navigateToMain();
                }
            }
        });

        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            if (errorMsg != null) Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        });

        authViewModel.getPasswordResetLiveData().observe(this, success -> {
            if (success != null && success) Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_LONG).show();
        });
    }

    private void setupUI() {
        if ("admin".equals(userRole)) {
            tvLoginTitle.setText("Đăng nhập Quản trị viên");
            emailEditText.setHint("ID Tài khoản");
            emailEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            googleSignInButton.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.GONE);
            forgotPasswordTextView.setVisibility(View.GONE);
        } else {
            tvLoginTitle.setText("Đăng nhập");
            emailEditText.setHint("Email");
            emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            googleSignInButton.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.VISIBLE);
            forgotPasswordTextView.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            authViewModel.firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showForgotPasswordDialog() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Cần có mạng để thực hiện chức năng này!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText resetEmail = new EditText(this);
        resetEmail.setHint("Nhập email của bạn");
        new AlertDialog.Builder(this)
                .setTitle("Quên mật khẩu")
                .setMessage("Nhập email để nhận liên kết đặt lại mật khẩu.")
                .setView(resetEmail)
                .setPositiveButton("Gửi", (dialog, which) -> {
                    String email = resetEmail.getText().toString();
                    if (!email.isEmpty()) authViewModel.resetPassword(email);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void checkAdminRole(String userId) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Cần có mạng để kiểm tra quyền Admin!", Toast.LENGTH_SHORT).show();
            authViewModel.signOut();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role)) {
                            Toast.makeText(LoginActivity.this, "Xin chào Admin!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Tài khoản không có quyền Admin!", Toast.LENGTH_LONG).show();
                            authViewModel.signOut();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi: Không tìm thấy hồ sơ.", Toast.LENGTH_SHORT).show();
                        authViewModel.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    authViewModel.signOut();
                });
    }
}
