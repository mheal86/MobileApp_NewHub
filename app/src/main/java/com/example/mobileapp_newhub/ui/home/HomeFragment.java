package com.example.mobileapp_newhub.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.adapter.PostAdapter;
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;

public class HomeFragment extends Fragment {

    private ReaderViewModel viewModel;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View searchBarContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerView();
        observeData();
        setupSwipeRefresh();
        setupSearch();

        return view;
    }

    private void initViews(View view) {
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        searchBarContainer = view.findViewById(R.id.searchBarContainer);
    }

    private void setupSearch() {
        if (searchBarContainer != null) {
            searchBarContainer.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_searchFragment);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Chưa cài đặt điều hướng tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            });
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
                        handleSaveClick(post);
                    }
                }
        );

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewPosts.setAdapter(postAdapter);
    }

    private void observeData() {
        viewModel.getAllPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postAdapter.setPosts(posts);
                progressBar.setVisibility(View.GONE);
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

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshPosts();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Đã làm mới", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePostClick(Post post) {
        viewModel.markPostAsViewed(post);

        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);

        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể mở bài viết", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveClick(Post post) {
        viewModel.toggleSavePost(post);
    }
}
