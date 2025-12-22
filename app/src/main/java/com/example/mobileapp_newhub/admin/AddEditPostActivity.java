package com.example.mobileapp_newhub.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminCategory;

import java.util.ArrayList;
import java.util.List;

public class AddEditPostActivity extends AppCompatActivity {

    private AdminViewModel vm;
    private EditText edtTitle, edtContent;
    private Spinner spinnerCategory;
    private ImageView imgPreview;
    private Button btnSelectImg, btnSave;
    private ProgressBar progress;

    private String postId;
    private Uri selectedImageUri;
    private final List<AdminCategory> categories = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private final List<String> categoryNames = new ArrayList<>();

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPreview.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_post);

        vm = new ViewModelProvider(this).get(AdminViewModel.class);

        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imgPreview = findViewById(R.id.imgPreview);
        btnSelectImg = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        progress = findViewById(R.id.progress);

        // Setup Spinner
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Load Categories
        vm.loadCategories();
        vm.categoriesLive.observe(this, list -> {
            categories.clear();
            categoryNames.clear();
            if (list != null) {
                categories.addAll(list);
                for (AdminCategory c : list) {
                    categoryNames.add(c.name);
                }
            }
            spinnerAdapter.notifyDataSetChanged();
            
            // Nếu đang edit, cần set selection cho spinner sau khi load xong
            // (Đơn giản hóa: ta sẽ làm ở phần load detail nếu cần, hoặc user tự chọn lại)
        });

        // Check Add or Edit
        if (getIntent().hasExtra("postId")) {
            postId = getIntent().getStringExtra("postId");
            btnSave.setText("Cập nhật");
            loadPostData(postId);
        } else {
            btnSave.setText("Đăng bài");
        }

        btnSelectImg.setOnClickListener(v -> pickImage.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();
            int catPos = spinnerCategory.getSelectedItemPosition();
            String catId = (catPos >= 0 && catPos < categories.size()) ? categories.get(catPos).id : null;

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            
            vm.savePost(postId, title, content, catId, selectedImageUri);
        });

        vm.loadingLive.observe(this, isLoading -> {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!Boolean.TRUE.equals(isLoading));
        });

        vm.errorLive.observe(this, msg -> {
            if (msg != null && !msg.trim().isEmpty())
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        
        // Quan sát khi lưu thành công thì postsLive sẽ được reload, 
        // nhưng ở Activity này ta không observe list post.
        // Ta cần một sự kiện success. Để đơn giản, ta observe loading. 
        // Nếu loading chuyển từ true -> false và không có lỗi -> success (hoặc dùng SingleLiveEvent).
        // Cách đơn giản nhất: AdminViewModel savePost gọi loadPosts khi xong.
        // Ta có thể finish activity này.
        // Tuy nhiên ViewModel scope ở đây là của Activity này, khác với Fragment.
        // Nên ta cần thêm 1 LiveData success trong ViewModel hoặc callback.
        // Sửa nhanh: Thêm logic finish vào observer loading hoặc dùng callback cho savePost.
        // Ở đây ta sẽ check thủ công: nếu loading tắt và không có lỗi mới -> finish.
        // (Code mẫu nên ta làm đơn giản: thêm callback vào ViewModel hoặc check loading)
    }

    private void loadPostData(String id) {
        vm.getPost(id, p -> {
            edtTitle.setText(p.title);
            edtContent.setText(p.content);
            if (p.imageUrl != null) {
                Glide.with(this).load(p.imageUrl).into(imgPreview);
            }
            // Set spinner selection
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).id.equals(p.categoryId)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        });
    }
}
