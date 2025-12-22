package com.example.mobileapp_newhub.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Post;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryPostsFragment extends Fragment {

    private ReaderViewModel viewModel;
    private Category category;
    
    private TextView txtTitle;
    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyView;
    private PostAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderViewModel.class);
        
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable("category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_posts, container, false);

        txtTitle = view.findViewById(R.id.txtCategoryTitle);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        if (category != null) {
            txtTitle.setText(category.name);
        }

        setupRecyclerView();

        // Observe all posts and filter by categoryId
        // Note: For better performance with large data, filtering should be done in ViewModel or Repository query
        viewModel.getAllPosts().observe(getViewLifecycleOwner(), posts -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            
            if (posts != null && category != null) {
                List<Post> filteredPosts = new ArrayList<>();
                for (Post p : posts) {
                    if (p.categoryId != null && p.categoryId.equals(category.id)) {
                        filteredPosts.add(p);
                    }
                }
                
                if (filteredPosts.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    rvPosts.setVisibility(View.VISIBLE);
                    adapter.setPosts(filteredPosts);
                }
            }
        });
        
        // Initial load check
        if (viewModel.getAllPosts().getValue() == null) {
            progressBar.setVisibility(View.VISIBLE);
            viewModel.refreshPosts();
        }

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshPosts());

        return view;
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter(
                requireContext(),
                this::openPostDetail,
                post -> viewModel.toggleSavePost(post)
        );
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setAdapter(adapter);
    }

    private void openPostDetail(Post post) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_categoryPostsFragment_to_detailFragment, bundle);
    }
}
