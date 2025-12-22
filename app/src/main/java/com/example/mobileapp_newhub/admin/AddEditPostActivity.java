package com.example.mobileapp_newhub.admin;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminCategory;
import com.example.mobileapp_newhub.admin.model.AdminPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddEditPostActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ProgressBar progress;
    private EditText edtTitle, edtSummary, edtContent;
    private Spinner spCategory;
    private Button btnPickImage, btnSave;

    private String postId;
    private Uri pickedImageUri;

    private String existingImageUrl, existingImagePath;

    private final List<AdminCategory> categories = new ArrayList<>();
    private final List<String> catNames = new ArrayList<>();
    private ArrayAdapter<String> catAdapter;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pickedImageUri = uri;
                    Toast.makeText(this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editor);

        db = FirebaseFirestore.getInstance();

        progress = findViewById(R.id.progress);
        edtTitle = findViewById(R.id.edtTitle);
        edtSummary = findViewById(R.id.edtSummary);
        edtContent = findViewById(R.id.edtContent);
        spCategory = findViewById(R.id.spCategory);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave = findViewById(R.id.btnSave);

        catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, catNames);
        spCategory.setAdapter(catAdapter);

        btnPickImage.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSave.setOnClickListener(v -> onSave());

        postId = getIntent().getStringExtra("postId");

        loadCategories(() -> {
            if (postId != null) loadPostForEdit(postId);
        });
    }

    private void loadCategories(Runnable done) {
        db.collection("categories")
                .get()
                .addOnSuccessListener(qs -> {
                    categories.clear();
                    catNames.clear();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        AdminCategory c = d.toObject(AdminCategory.class);
                        if (c != null) {
                            c.id = d.getId();
                            categories.add(c);
                            catNames.add(c.name);
                        }
                    }
                    if (catNames.isEmpty()) {
                        catNames.add("Chưa có danh mục (hãy tạo ở Admin)");
                        btnSave.setEnabled(false);
                    } else {
                        btnSave.setEnabled(true);
                    }
                    catAdapter.notifyDataSetChanged();
                    done.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Load categories failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    done.run();
                });
    }

    private void loadPostForEdit(String id) {
        progress.setVisibility(View.VISIBLE);
        db.collection("posts").document(id)
                .get()
                .addOnSuccessListener(d -> {
                    progress.setVisibility(View.GONE);
                    AdminPost p = d.toObject(AdminPost.class);
                    if (p == null) return;

                    edtTitle.setText(p.title);
                    edtSummary.setText(p.summary);
                    edtContent.setText(p.content);
                    existingImageUrl = p.imageUrl;
                    existingImagePath = p.imagePath;

                    int idx = 0;
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).id != null && categories.get(i).id.equals(p.categoryId)) {
                            idx = i;
                            break;
                        }
                    }
                    spCategory.setSelection(idx);
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Load post failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void onSave() {
        if (categories.isEmpty()) {
            Toast.makeText(this, "Hãy tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = edtTitle.getText().toString().trim();
        String summary = edtSummary.getText().toString().trim();
        String content = edtContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) { edtTitle.setError("Nhập tiêu đề"); return; }
        if (TextUtils.isEmpty(content)) { edtContent.setError("Nhập nội dung"); return; }

        int sel = spCategory.getSelectedItemPosition();
        AdminCategory cat = categories.get(Math.max(0, Math.min(sel, categories.size() - 1)));

        progress.setVisibility(View.VISIBLE);

        if (pickedImageUri != null) {
            AdminStorage.uploadPostImage(pickedImageUri,
                    (downloadUrl, storagePath) -> {
                        // nếu đang edit thì xoá ảnh cũ trước
                        AdminStorage.deleteImageByPath(existingImagePath,
                                () -> savePost(title, summary, content, cat, downloadUrl, storagePath),
                                err -> savePost(title, summary, content, cat, downloadUrl, storagePath)
                        );
                    },
                    err -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(this, "Upload image failed: " + err, Toast.LENGTH_LONG).show();
                    }
            );
        } else {
            savePost(title, summary, content, cat, existingImageUrl, existingImagePath);
        }
    }

    private void savePost(String title, String summary, String content, AdminCategory cat,
                          String imageUrl, String imagePath) {

        String uid = FirebaseAuth.getInstance().getUid();

        AdminPost p = new AdminPost();
        p.title = title;
        p.summary = summary;
        p.content = content;
        p.categoryId = cat.id;
        p.categoryName = cat.name;
        p.imageUrl = imageUrl;
        p.imagePath = imagePath;
        p.authorId = uid;

        if (postId == null) {
            db.collection("posts")
                    .add(p.toMap(true))
                    .addOnSuccessListener(ref -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(this, "Đã đăng bài", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(this, "Publish failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            db.collection("posts").document(postId)
                    .update(p.toMap(false))
                    .addOnSuccessListener(v -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}

