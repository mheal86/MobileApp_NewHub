package com.example.mobileapp_newhub.ui.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;

public class BookmarkFragment extends Fragment {

    private ReaderViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private PostAdapter postAdapter;

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
        observeData();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewBookmarkedPosts);
        emptyTextView = view.findViewById(R.id.textViewEmpty);
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
        // Không cần gọi loadSavedPosts() ở đây vì ReaderViewModel đã dùng MutableLiveData
        // và repository.toggleBookmark cập nhật lại danh sách.
        // Khi toggleBookmark thành công, savedPosts được setValue() mới,
        // UI ở Fragment này sẽ tự nhận được update nhờ observe().
    }

    private void observeData() {
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
        // Khi bấm nút lưu/bỏ lưu ở màn hình Bookmark:
        // Logic toggleSavePost trong ViewModel sẽ gọi Repository.
        // Repository cập nhật Firestore/Room, sau đó gọi loadSavedPosts() lại.
        // LiveData savedPosts thay đổi -> UI tự cập nhật (xóa item khỏi list nếu bỏ lưu).
        viewModel.toggleSavePost(post);
        // Toast.makeText(requireContext(), "Đang cập nhật...", Toast.LENGTH_SHORT).show();
    }
}
