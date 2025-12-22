package com.example.mobileapp_newhub.auth;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthViewModel extends AndroidViewModel {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mDb;
    private final GoogleSignInClient mGoogleSignInClient;

    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordResetLiveData = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(application, gso);

        if (mAuth.getCurrentUser() != null) {
            userLiveData.postValue(mAuth.getCurrentUser());
            loadUserProfile();
        }
    }

    // GETTERS
    public LiveData<FirebaseUser> getUserLiveData() { return userLiveData; }
    public LiveData<User> getUserProfileLiveData() { return userProfileLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getPasswordResetLiveData() { return passwordResetLiveData; }
    
    // METHOD MỚI ĐỂ LẤY GOOGLE SIGN IN INTENT
    public Intent getGoogleSignInIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(mAuth.getCurrentUser());
                        loadUserProfile();
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    public void register(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Logic lưu role="user" ở đây là ĐÚNG
                            User newUser = new User(firebaseUser.getUid(), name, email, "user", "");
                            saveUserToFirestore(newUser);
                        }
                    } else {
                        // Hiển thị lỗi rõ ràng hơn
                        if (task.getException() != null) {
                            errorLiveData.postValue(task.getException().getMessage());
                        } else {
                            errorLiveData.postValue("Đăng ký thất bại.");
                        }
                    }
                });
    }

    public void firebaseAuthWithGoogle(com.google.android.gms.auth.api.signin.GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        mDb.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(docTask -> {
                            if (!docTask.getResult().exists()) {
                                String photoUrl = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "";
                                User newUser = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), "user", photoUrl);
                                saveUserToFirestore(newUser);
                            } else {
                                userLiveData.postValue(firebaseUser);
                                loadUserProfile();
                            }
                        });
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }
    
    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        passwordResetLiveData.postValue(true);
                    } else {
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    private void saveUserToFirestore(User user) {
        mDb.collection("users").document(user.getUid()).set(user)
                .addOnSuccessListener(aVoid -> {
                    userLiveData.postValue(mAuth.getCurrentUser());
                    loadUserProfile();
                })
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }
    
    public void uploadAvatar(Uri uri) {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("avatars").child(uid + ".jpg");

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String url = downloadUri.toString();
                        mDb.collection("users").document(uid).update("photoUrl", url);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates);
                        loadUserProfile();
                    });
                })
                .addOnFailureListener(e -> errorLiveData.postValue("Lỗi upload ảnh: " + e.getMessage()));
    }

    public void updateUserProfile(String name, List<String> interests) {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("interests", interests);

        mDb.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> loadUserProfile())
                .addOnFailureListener(e -> errorLiveData.postValue("Lỗi cập nhật: " + e.getMessage()));
    }

    public void changePassword(String newPassword) {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> errorLiveData.postValue("Đổi mật khẩu thành công"))
                    .addOnFailureListener(e -> errorLiveData.postValue("Lỗi: " + e.getMessage()));
        }
    }

    public void logout() {
        signOut();
    }

    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            userLiveData.postValue(null);
            userProfileLiveData.postValue(null);
        });
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            mDb.collection("users").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            userProfileLiveData.postValue(user);
                        }
                    });
        }
    }
}