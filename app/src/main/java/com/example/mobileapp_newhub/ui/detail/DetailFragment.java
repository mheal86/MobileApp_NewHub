package com.example.mobileapp_newhub.ui.detail;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.viewmodel.ReaderViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private ReaderViewModel viewModel;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView categoryTextView;
    private TextView contentTextView;
    private ImageButton saveButton;
    private Post currentPost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderViewModel.class);

        if (getArguments() != null) {
            currentPost = (Post) getArguments().getSerializable("post");
        }

        if (savedInstanceState != null && currentPost == null) {
            currentPost = (Post) savedInstanceState.getSerializable("post");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initViews(view);

        if (currentPost != null) {
            displayPost(currentPost);
            viewModel.markPostAsViewed(currentPost);
        }

        setupSaveButton();
        observeFontSize();

        return view;
    }

    private void initViews(View view) {
        imageView = view.findViewById(R.id.imageViewPost);
        titleTextView = view.findViewById(R.id.textViewTitle);
        authorTextView = view.findViewById(R.id.textViewAuthor);
        dateTextView = view.findViewById(R.id.textViewDate);
        categoryTextView = view.findViewById(R.id.textViewCategory);
        contentTextView = view.findViewById(R.id.textViewContent);
        saveButton = view.findViewById(R.id.buttonSave);
    }

    private void displayPost(Post post) {
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder);
        }

        titleTextView.setText(post.getTitle());
        authorTextView.setText("Tác giả: " + post.getAuthor());
        categoryTextView.setText("Danh mục: " + post.getCategory());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        dateTextView.setText(sdf.format(new Date(post.getTimestamp())));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            contentTextView.setText(Html.fromHtml(post.getContent(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            contentTextView.setText(Html.fromHtml(post.getContent()));
        }

        updateSaveButtonIcon(post.isSaved());
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPost != null) {
                    viewModel.toggleSavePost(currentPost);
                    boolean newStatus = !currentPost.isSaved();
                    currentPost.setSaved(newStatus);
                    updateSaveButtonIcon(newStatus);

                    String message = newStatus ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSaveButtonIcon(boolean isSaved) {
        if (isSaved) {
            saveButton.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            saveButton.setImageResource(R.drawable.ic_bookmark_outline);
        }
    }

    private void observeFontSize() {
        viewModel.getFontSize().observe(getViewLifecycleOwner(), fontSize -> {
            if (fontSize != null) {
                titleTextView.setTextSize(fontSize + 4);
                contentTextView.setTextSize(fontSize);
                authorTextView.setTextSize(fontSize - 2);
                dateTextView.setTextSize(fontSize - 2);
                categoryTextView.setTextSize(fontSize - 2);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPost != null) {
            outState.putSerializable("post", currentPost);
        }
    }
}