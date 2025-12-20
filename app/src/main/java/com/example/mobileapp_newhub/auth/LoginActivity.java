package com.example.mobileapp_newhub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp_newhub.MainActivity;
import com.example.mobileapp_newhub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText emailEditText, passwordEditText;
    private GoogleSignInClient mGoogleSignInClient;
    
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

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button googleSignInButton = findViewById(R.id.googleSignInButton);
        TextView registerTextView = findViewById(R.id.registerTextView);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Configure Google Sign In
        // You need to replace R.string.default_web_client_id with your actual client ID or ensure google-services.json is present
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) 
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                authViewModel.login(email, password);
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
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

        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                startActivity(new Intent(this, MainActivity.class));
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