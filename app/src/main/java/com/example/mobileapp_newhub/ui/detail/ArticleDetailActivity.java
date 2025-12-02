package com.example.mobileapp_newhub.ui.detail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp_newhub.R;

public class ArticleDetailActivity extends AppCompatActivity {

    TextView txtDetailTitle, txtDetailMeta, txtDetailContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        txtDetailTitle = findViewById(R.id.txtDetailTitle);
        txtDetailMeta = findViewById(R.id.txtDetailMeta);
        txtDetailContent = findViewById(R.id.txtDetailContent);

        txtDetailTitle.setText(getIntent().getStringExtra("title"));
        txtDetailMeta.setText(
                getIntent().getStringExtra("category")
                        + " • "
                        + getIntent().getStringExtra("time")
        );
        txtDetailContent.setText(getIntent().getStringExtra("content"));
    }
}
