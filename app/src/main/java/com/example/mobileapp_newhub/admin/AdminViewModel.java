package com.example.mobileapp_newhub.admin;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp_newhub.admin.model.AdminCategory;
import com.example.mobileapp_newhub.admin.model.AdminPost;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public final MutableLiveData<List<AdminPost>> postsLive = new MutableLiveData<>();
    public final MutableLiveData<List<AdminCategory>> categoriesLive = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loadingLive = new MutableLiveData<>(false);
    public final MutableLiveData<String> errorLive = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) { super(application); }

    // POSTS
    public void loadPosts() {
        loadingLive.setValue(true);
        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(200)
                .get()
                .addOnSuccessListener(qs -> {
                    List<AdminPost> list = new ArrayList<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        AdminPost p = d.toObject(AdminPost.class);
                        if (p != null) { p.id = d.getId(); list.add(p); }
                    }
                    postsLive.setValue(list);
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Load posts failed: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void deletePost(AdminPost post) {
        if (post == null || post.id == null) return;
        loadingLive.setValue(true);

        db.collection("posts").document(post.id)
                .delete()
                .addOnSuccessListener(v -> {
                    AdminStorage.deleteImageByPath(post.imagePath,
                            () -> { loadingLive.setValue(false); loadPosts(); },
                            err -> {
                                errorLive.setValue("Post deleted but image delete failed: " + err);
                                loadingLive.setValue(false);
                                loadPosts();
                            });
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Delete failed: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    // CATEGORIES
    public void loadCategories() {
        loadingLive.setValue(true);
        db.collection("categories")
                .get()
                .addOnSuccessListener(qs -> {
                    List<AdminCategory> list = new ArrayList<>();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        AdminCategory c = d.toObject(AdminCategory.class);
                        if (c != null) { c.id = d.getId(); list.add(c); }
                    }
                    categoriesLive.setValue(list);
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Load categories failed: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            errorLive.setValue("Category name is empty");
            return;
        }
        loadingLive.setValue(true);
        AdminCategory c = new AdminCategory();
        c.name = name.trim();

        db.collection("categories")
                .add(c.toMap(true))
                .addOnSuccessListener(ref -> { loadingLive.setValue(false); loadCategories(); })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Add category failed: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void deleteCategory(AdminCategory c) {
        if (c == null || c.id == null) return;
        loadingLive.setValue(true);
        db.collection("categories").document(c.id)
                .delete()
                .addOnSuccessListener(v -> { loadingLive.setValue(false); loadCategories(); })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Delete category failed: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    // IMAGE UPLOAD helper (AddEditPostActivity dùng)
    public void uploadImage(Uri uri, AdminStorage.OnUploadOk ok, AdminStorage.OnErr err) {
        AdminStorage.uploadPostImage(uri, ok, err);
    }
}
