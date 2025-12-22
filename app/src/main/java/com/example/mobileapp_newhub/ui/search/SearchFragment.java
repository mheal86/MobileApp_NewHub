package com.example.mobileapp_newhub.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class SearchFragment extends Fragment {

    private ReaderViewModel viewModel;
    private EditText searchEditText;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Fix: Inflate fragment_search instead of fragment_settings
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchBar();
        observeSearchResults();

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.editTextSearch);
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
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
                        handleSaveClick(post);
                    }
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(postAdapter);
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.length() > 0) {
                    viewModel.search(query);
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    postAdapter.setPosts(null);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("Nhập từ khóa để tìm kiếm");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void observeSearchResults() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                postAdapter.setPosts(posts);
                emptyTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else if (searchEditText.getText().toString().trim().length() > 0) {
                postAdapter.setPosts(null);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Không tìm thấy kết quả nào");
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
                    .navigate(R.id.action_searchFragment_to_detailFragment, bundle);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể mở bài viết", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSaveClick(Post post) {
        viewModel.toggleSavePost(post);
        String message = post.isSaved() ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
