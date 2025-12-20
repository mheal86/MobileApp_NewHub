package com.example.mobileapp_newhub.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment implements AdminPostAdapter.Listener {

    private AdminViewModel vm;
    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private ProgressBar progress;
    private FloatingActionButton fabAdd, fabCategories;

    private AdminPostAdapter adapter;
    private final List<AdminPost> items = new ArrayList<>();

    public AdminHomeFragment() {
        super(R.layout.fragment_admin_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(AdminViewModel.class);

        swipe = view.findViewById(R.id.swipe);
        rv = view.findViewById(R.id.rvPosts);
        progress = view.findViewById(R.id.progress);
        fabAdd = view.findViewById(R.id.fabAdd);
        fabCategories = view.findViewById(R.id.fabCategories);

        adapter = new AdminPostAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(vm::loadPosts);

        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), AddEditPostActivity.class);
            startActivity(i);
        });

        fabCategories.setOnClickListener(v -> {
            // IMPORTANT: đổi R.id.main_container theo container thật của app bạn
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_main, new CategoryFragment())
                    .addToBackStack("admin_categories")
                    .commit();
        });

        vm.postsLive.observe(getViewLifecycleOwner(), list -> {
            items.clear();
            if (list != null) items.addAll(list);
            adapter.notifyDataSetChanged();
            swipe.setRefreshing(false);
        });

        vm.loadingLive.observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        vm.errorLive.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.trim().isEmpty())
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        vm.loadPosts();
    }

    @Override
    public void onEdit(AdminPost post) {
        Intent i = new Intent(requireContext(), AddEditPostActivity.class);
        i.putExtra("postId", post.id);
        startActivity(i);
    }

    @Override
    public void onDelete(AdminPost post) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá bài viết?")
                .setMessage("Bài viết sẽ bị xoá vĩnh viễn. Ảnh trên Storage cũng sẽ bị xoá.")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (d, w) -> vm.deletePost(post))
                .show();
    }
}
