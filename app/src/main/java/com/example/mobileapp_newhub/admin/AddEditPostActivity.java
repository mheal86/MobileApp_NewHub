package com.example.mobileapp_newhub.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp_newhub.R;

public class AddEditPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_post);

        EditText edtTitle = findViewById(R.id.edtTitle);
        EditText edtImageUrl = findViewById(R.id.edtImageUrl);
        EditText edtContent = findViewById(R.id.edtContent);
        Button btnSavePost = findViewById(R.id.btnSavePost);

        btnSavePost.setOnClickListener(v -> {
            Toast.makeText(this, "Bài viết đã được lưu!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}