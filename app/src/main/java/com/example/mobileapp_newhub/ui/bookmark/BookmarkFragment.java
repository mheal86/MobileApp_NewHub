package com.example.mobileapp_newhub.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.adapter.PostAdapter;
import com.example.mobileapp_newhub.auth.LoginActivity;
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BookmarkFragment extends Fragment {

    private ReaderViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private PostAdapter postAdapter;
    
    // UI Layouts cho Guest
    private View layoutLoggedIn;
    private LinearLayout layoutGuest;
    private Button btnBookmarkLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        initViews(view);
        setupRecyclerView();
        
        // Kiểm tra đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUIState(currentUser);

        // Chỉ observe data nếu đã đăng nhập
        if (currentUser != null) {
            observeData();
            // Đảm bảo load dữ liệu
            viewModel.loadSavedPosts();
        }

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewBookmarkedPosts);
        emptyTextView = view.findViewById(R.id.textViewEmpty);
        
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn);
        layoutGuest = view.findViewById(R.id.layoutGuest);
        btnBookmarkLogin = view.findViewById(R.id.btnBookmarkLogin);
        
        btnBookmarkLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });
    }
    
    private void updateUIState(FirebaseUser user) {
        if (user != null) {
            layoutLoggedIn.setVisibility(View.VISIBLE);
            layoutGuest.setVisibility(View.GONE);
        } else {
            layoutLoggedIn.setVisibility(View.GONE);
            layoutGuest.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                requireContext(),
                new PostAdapter.OnPostClickListener() {
                    @Override
                    public void onPostClick(Post post) {
                        handlePostClick(post);
                    }
                },
                new PostAdapter.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(Post post) {
                        handleBookmarkClick(post);
                    }
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại UI khi quay lại (ví dụ sau khi đăng nhập xong)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUIState(currentUser);
        if (currentUser != null) {
            observeData();
            viewModel.loadSavedPosts();
        }
    }

    private void observeData() {
        // Tránh observe nhiều lần nếu đã observe rồi (ViewModelProvider giữ instance ViewModel)
        // Tuy nhiên với Fragment Lifecycle, observe với getViewLifecycleOwner() là an toàn.
        
        viewModel.getSavedPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                postAdapter.setPosts(posts);
                emptyTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                postAdapter.setPosts(null);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Chưa có bài viết đã lưu");
                recyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.getFontSize().observe(getViewLifecycleOwner(), fontSize -> {
            if (postAdapter != null && fontSize != null) {
                postAdapter.setFontSize(fontSize);
            }
        });

        viewModel.isDarkMode().observe(getViewLifecycleOwner(), isDarkMode -> {
            if (isDarkMode != null && postAdapter != null) {
                postAdapter.setDarkMode(isDarkMode);
            }
        });
    }

    private void handlePostClick(Post post) {
        viewModel.markPostAsViewed(post);

        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);

        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_bookmarkFragment_to_detailFragment, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể mở bài viết", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleBookmarkClick(Post post) {
        viewModel.toggleSavePost(post);
    }
}
