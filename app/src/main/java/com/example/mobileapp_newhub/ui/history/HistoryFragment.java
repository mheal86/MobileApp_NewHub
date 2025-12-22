package com.example.mobileapp_newhub.ui.history;

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

public class HistoryFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        emptyTextView = view.findViewById(R.id.textViewEmptyHistory);

        setupRecyclerView();
        observeData();
        
        // Tải lại lịch sử mỗi khi vào màn hình này để đảm bảo mới nhất
        viewModel.loadHistoryPosts();

        return view;
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(
                requireContext(),
                this::handlePostClick,
                post -> viewModel.toggleSavePost(post)
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter);
    }

    private void observeData() {
        viewModel.getHistoryPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                postAdapter.setPosts(posts);
                emptyTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                postAdapter.setPosts(null);
                emptyTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
        
        // Đồng bộ setting chữ/màu tối
        viewModel.getFontSize().observe(getViewLifecycleOwner(), size -> postAdapter.setFontSize(size));
        viewModel.isDarkMode().observe(getViewLifecycleOwner(), isDark -> postAdapter.setDarkMode(isDark));
    }

    private void handlePostClick(Post post) {
        viewModel.markPostAsViewed(post);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_historyFragment_to_detailFragment, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi chuyển trang", Toast.LENGTH_SHORT).show();
        }
    }
}
