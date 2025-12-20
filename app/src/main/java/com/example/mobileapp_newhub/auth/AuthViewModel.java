package com.example.mobileapp_newhub.auth;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp_newhub.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AuthViewModel extends AndroidViewModel {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<User> userProfileLiveData;
    private final MutableLiveData<String> errorLiveData;
    private final MutableLiveData<Boolean> passwordResetLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        userLiveData = new MutableLiveData<>();
        userProfileLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        passwordResetLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            loadUserProfile(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<User> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getPasswordResetLiveData() {
        return passwordResetLiveData;
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        loadUserProfile(firebaseAuth.getCurrentUser().getUid());
                    } else {
                        if (task.getException() != null) {
                            errorLiveData.postValue(task.getException().getMessage());
                        }
                        userLiveData.postValue(null);
                    }
                });
    }

    public void register(String email, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        userLiveData.postValue(firebaseUser);
                        if (firebaseUser != null) {
                            createUserProfile(firebaseUser.getUid(), email, name, null);
                        }
                    } else {
                        if (task.getException() != null) {
                            errorLiveData.postValue(task.getException().getMessage());
                        }
                        userLiveData.postValue(null);
                    }
                });
    }

    public void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordResetLiveData.postValue(true);
                    } else {
                        if (task.getException() != null) {
                            errorLiveData.postValue(task.getException().getMessage());
                        }
                        passwordResetLiveData.postValue(false);
                    }
                });
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        userLiveData.postValue(user);
                        if (user != null) {
                            firestore.collection("users").document(user.getUid()).get()
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            DocumentSnapshot document = profileTask.getResult();
                                            if (!document.exists()) {
                                                createUserProfile(user.getUid(), user.getEmail(), user.getDisplayName(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                            } else {
                                                loadUserProfile(user.getUid());
                                            }
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() != null) {
                            errorLiveData.postValue(task.getException().getMessage());
                        }
                        userLiveData.postValue(null);
                    }
                });
    }

    private void createUserProfile(String uid, String email, String name, String photoUrl) {
        User user = new User(uid, name, email, photoUrl != null ? photoUrl : "", "user");
        firestore.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> userProfileLiveData.postValue(user));
    }

    private void loadUserProfile(String uid) {
        firestore.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            userProfileLiveData.postValue(user);
                        }
                    }
                });
    }

    public void uploadAvatar(Uri imageUri) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        StorageReference storageRef = storage.getReference().child("avatars/" + uid + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String photoUrl = uri.toString();
                        updateUserProfilePhoto(uid, photoUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    errorLiveData.postValue("Failed to upload avatar: " + e.getMessage());
                });
    }

    private void updateUserProfilePhoto(String uid, String photoUrl) {
        firestore.collection("users").document(uid)
                .update("photoUrl", photoUrl)
                .addOnSuccessListener(aVoid -> {
                    loadUserProfile(uid); // Reload to update UI
                })
                .addOnFailureListener(e -> errorLiveData.postValue("Failed to update profile: " + e.getMessage()));
    }
    
    public void updateUserProfile(String newName, List<String> interests) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String uid = currentUser.getUid();
        
        // Update Firestore
        firestore.collection("users").document(uid)
                .update(
                        "name", newName,
                        "interests", interests
                )
                .addOnSuccessListener(aVoid -> {
                    loadUserProfile(uid);
                    errorLiveData.postValue("Profile updated successfully");
                })
                .addOnFailureListener(e -> errorLiveData.postValue("Failed to update profile: " + e.getMessage()));
    }

    // Keep old method for compatibility if needed, but better to use the new one.
    public void updateUserName(String newName) {
        // Redirect to new method with null interests (or keep current interests if logic allows, 
        // but for now simpler to just update name)
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;
        firestore.collection("users").document(currentUser.getUid()).update("name", newName)
             .addOnSuccessListener(aVoid -> loadUserProfile(currentUser.getUid()));
    }

    public void changePassword(String newPassword) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            errorLiveData.postValue("Password updated successfully");
                        } else {
                            // Re-authentication might be needed if login was long ago
                            errorLiveData.postValue("Failed to update password: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error. Try logging out and in again."));
                        }
                    });
        }
    }

    public void logout() {
        firebaseAuth.signOut();
        userLiveData.postValue(null);
        userProfileLiveData.postValue(null);
    }
}