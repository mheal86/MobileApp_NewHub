package com.example.mobileapp_newhub.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.adapter.CommentAdapter;
import com.example.mobileapp_newhub.auth.AuthViewModel;
import com.example.mobileapp_newhub.model.Comment;
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.model.User;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DetailFragment extends Fragment {

    private ReaderViewModel viewModel;
    private AuthViewModel authViewModel;
    
    private ImageView imageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private TextView categoryTextView;
    private TextView contentTextView;
    private ImageButton saveButton;
    private ImageButton shareButton; // Nút Share mới
    
    // Comments UI
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private ImageButton btnSendComment;
    private RatingBar ratingBarInput;
    
    private Post currentPost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

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
        setupCommentsRecyclerView();

        if (currentPost != null) {
            displayPost(currentPost);
            viewModel.markPostAsViewed(currentPost);
            viewModel.loadComments(currentPost.getId());
        }

        setupSaveButton();
        setupShareButton(); // Setup nút Share
        setupCommentInput();
        
        observeFontSize();
        observeComments();

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
        shareButton = view.findViewById(R.id.buttonShare); // Init Share Button
        
        // Comment views
        rvComments = view.findViewById(R.id.rvComments);
        etComment = view.findViewById(R.id.etComment);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        ratingBarInput = view.findViewById(R.id.ratingBarInput);
    }
    
    private void setupCommentsRecyclerView() {
        commentAdapter = new CommentAdapter(requireContext());
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(commentAdapter);
    }

    private void displayPost(Post post) {
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(post.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
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

    private void setupShareButton() {
        shareButton.setOnClickListener(v -> {
            if (currentPost != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                
                // Tạo nội dung chia sẻ (Tiêu đề + Link/Nội dung tóm tắt)
                String shareBody = currentPost.getTitle() + "\n\n" + 
                                   "Đọc bài viết tại: https://example.com/post/" + currentPost.getId(); // Giả lập link
                String shareSub = currentPost.getTitle();
                
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                
                startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua"));
            }
        });
    }
    
    private void setupCommentInput() {
        btnSendComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            float rating = ratingBarInput.getRating();
            
            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                return;
            }
            
            User currentUser = authViewModel.getUserProfileLiveData().getValue();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            
            if (currentUser == null && firebaseUser == null) {
                Toast.makeText(requireContext(), "Bạn cần đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String userId = firebaseUser != null ? firebaseUser.getUid() : "guest";
            String userName = currentUser != null ? currentUser.getName() : "Anonymous";
            String userAvatar = currentUser != null ? currentUser.getPhotoUrl() : null;
            
            Comment newComment = new Comment(
                    userId,
                    userName,
                    userAvatar,
                    content,
                    rating,
                    System.currentTimeMillis()
            );
            newComment.setId(UUID.randomUUID().toString());
            
            if (currentPost != null) {
                viewModel.addComment(currentPost.getId(), newComment);
                etComment.setText("");
                ratingBarInput.setRating(5.0f);
                Toast.makeText(requireContext(), "Đã gửi bình luận!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSaveButtonIcon(boolean isSaved) {
        if (isSaved) {
            saveButton.setImageResource(android.R.drawable.star_on);
        } else {
            saveButton.setImageResource(android.R.drawable.star_off);
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
    
    private void observeComments() {
        viewModel.getCurrentPostComments().observe(getViewLifecycleOwner(), comments -> {
            commentAdapter.setComments(comments);
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
