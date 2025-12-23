package com.example.mobileapp_newhub.admin;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.mobileapp_newhub.admin.model.AdminCategory;
import com.example.mobileapp_newhub.admin.model.AdminPost;
import com.example.mobileapp_newhub.admin.model.AdminUser;
import com.example.mobileapp_newhub.utils.CloudinaryHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminViewModel extends AndroidViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public final MutableLiveData<List<AdminPost>> postsLive = new MutableLiveData<>();
    public final MutableLiveData<List<AdminCategory>> categoriesLive = new MutableLiveData<>();
    public final MutableLiveData<List<AdminUser>> usersLive = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loadingLive = new MutableLiveData<>(false);
    public final MutableLiveData<String> errorLive = new MutableLiveData<>();
    public final MutableLiveData<Boolean> saveSuccessLive = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        CloudinaryHelper.init(application);
    }

    // --- CATEGORIES ---
    public void loadCategories() {
        loadingLive.setValue(true);
        db.collection("categories").orderBy("name").get()
                .addOnSuccessListener(snaps -> {
                    List<AdminCategory> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snaps) {
                        AdminCategory c = doc.toObject(AdminCategory.class);
                        if (c != null) {
                            c.id = doc.getId();
                            list.add(c);
                        }
                    }
                    categoriesLive.setValue(list);
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi tải danh mục: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void addCategory(String name) {
        if (name.isEmpty()) return;
        loadingLive.setValue(true);
        AdminCategory c = new AdminCategory(null, name);
        db.collection("categories").add(c)
                .addOnSuccessListener(doc -> {
                    loadCategories();
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi thêm danh mục: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void updateCategory(String id, String newName) {
        if (id == null || newName.isEmpty()) return;
        loadingLive.setValue(true);
        db.collection("categories").document(id)
                .update("name", newName)
                .addOnSuccessListener(v -> {
                    loadCategories();
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi cập nhật danh mục: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void deleteCategory(AdminCategory c) {
        loadingLive.setValue(true);
        db.collection("categories").document(c.id).delete()
                .addOnSuccessListener(v -> {
                    loadCategories();
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi xoá danh mục: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }
    
    // --- POSTS ---
    public void loadPosts() {
        loadingLive.setValue(true);
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snaps -> {
                    List<AdminPost> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snaps) {
                        AdminPost p = doc.toObject(AdminPost.class);
                        if (p != null) {
                            p.id = doc.getId();
                            list.add(p);
                        }
                    }
                    postsLive.setValue(list);
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi tải bài viết: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void savePost(String id, String title, String content, String categoryId, Uri imageUri) {
        loadingLive.setValue(true);
        saveSuccessLive.setValue(false);

        if (imageUri != null) {
            MediaManager.get().upload(imageUri)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {}
                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imageUrl = (String) resultData.get("secure_url");
                            if (imageUrl == null) imageUrl = (String) resultData.get("url");
                            savePostToFirestore(id, title, content, categoryId, imageUrl);
                        }
                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            errorLive.postValue("Lỗi upload ảnh Cloudinary: " + error.getDescription());
                            loadingLive.postValue(false);
                        }
                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {}
                    })
                    .dispatch();
        } else {
            savePostToFirestore(id, title, content, categoryId, null);
        }
    }

    private void savePostToFirestore(String id, String title, String content, String categoryId, String imageUrl) {
        if (id != null) {
            db.collection("posts").document(id)
                    .update("title", title, "content", content, "categoryId", categoryId)
                    .addOnSuccessListener(v -> {
                         if (imageUrl != null) {
                             db.collection("posts").document(id).update("imageUrl", imageUrl);
                         }
                         loadPosts();
                         loadingLive.setValue(false);
                         saveSuccessLive.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        errorLive.setValue("Lỗi cập nhật: " + e.getMessage());
                        loadingLive.setValue(false);
                    });
        } else {
            AdminPost p = new AdminPost();
            p.title = title;
            p.content = content;
            p.categoryId = categoryId;
            p.timestamp = System.currentTimeMillis();
            p.imageUrl = imageUrl;
            db.collection("posts").add(p)
                    .addOnSuccessListener(doc -> {
                        loadPosts();
                        loadingLive.setValue(false);
                        saveSuccessLive.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        errorLive.setValue("Lỗi thêm mới: " + e.getMessage());
                        loadingLive.setValue(false);
                    });
        }
    }
    
    public void getPost(String id, OnPostLoaded callback) {
        db.collection("posts").document(id).get().addOnSuccessListener(doc -> {
             AdminPost p = doc.toObject(AdminPost.class);
             if(p != null) {
                 p.id = doc.getId();
                 callback.onLoaded(p);
             }
        });
    }
    
    public interface OnPostLoaded {
        void onLoaded(AdminPost p);
    }

    public void deletePost(AdminPost post) {
        loadingLive.setValue(true);
        db.collection("posts").document(post.id).delete()
                .addOnSuccessListener(v -> {
                    loadPosts();
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi xoá: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    // --- USERS ---
    public void loadUsers() {
        loadingLive.setValue(true);
        db.collection("users").get()
                .addOnSuccessListener(snaps -> {
                    List<AdminUser> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snaps) {
                        AdminUser u = doc.toObject(AdminUser.class);
                        if (u != null) {
                            u.id = doc.getId();
                            list.add(u);
                        }
                    }
                    usersLive.setValue(list);
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi tải user: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }

    public void deleteUser(AdminUser u) {
        loadingLive.setValue(true);
        db.collection("users").document(u.id).delete()
                .addOnSuccessListener(v -> {
                    loadUsers();
                    loadingLive.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorLive.setValue("Lỗi xoá user: " + e.getMessage());
                    loadingLive.setValue(false);
                });
    }
}
