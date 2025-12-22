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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.ui.viewmodel.ReaderViewModel;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private ReaderViewModel viewModel;
    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private TextView emptyView;

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
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        rvCategories = view.findViewById(R.id.rvCategories);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 cột

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            progressBar.setVisibility(View.GONE);
            if (categories != null && !categories.isEmpty()) {
                CategoryGridAdapter adapter = new CategoryGridAdapter(categories, this::onCategoryClick);
                rvCategories.setAdapter(adapter);
                rvCategories.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                rvCategories.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
        
        viewModel.loadCategories();

        return view;
    }

    private void onCategoryClick(Category category) {
        // Điều hướng sang màn hình danh sách bài viết theo danh mục
        // Cần tạo CategoryPostsFragment và action trong nav_graph
        // Tạm thời thông báo
        // Toast.makeText(requireContext(), "Clicked: " + category.name, Toast.LENGTH_SHORT).show();
        
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        // Kiểm tra xem action có tồn tại chưa, nếu chưa thì catch
        try {
             Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_categoryPostsFragment, bundle);
        } catch (Exception e) {
             Toast.makeText(requireContext(), "Chưa cài đặt action navigation", Toast.LENGTH_SHORT).show();
        }
    }
}
