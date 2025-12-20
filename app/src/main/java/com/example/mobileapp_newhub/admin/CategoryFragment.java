package com.example.mobileapp_newhub.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminCategory;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryAdapter.Listener {

    private AdminViewModel vm;
    private RecyclerView rv;
    private ProgressBar progress;
    private EditText edtName;
    private MaterialButton btnAdd;

    private final List<AdminCategory> items = new ArrayList<>();
    private CategoryAdapter adapter;

    public CategoryFragment() {
        super(R.layout.fragment_admin_category);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(AdminViewModel.class);

        rv = view.findViewById(R.id.rvCategories);
        progress = view.findViewById(R.id.progress);
        edtName = view.findViewById(R.id.edtCategoryName);
        btnAdd = view.findViewById(R.id.btnAddCategory);

        adapter = new CategoryAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            vm.addCategory(name);
            edtName.setText("");
        });

        vm.categoriesLive.observe(getViewLifecycleOwner(), list -> {
            items.clear();
            if (list != null) items.addAll(list);
            adapter.notifyDataSetChanged();
        });

        vm.loadingLive.observe(getViewLifecycleOwner(), isLoading -> {
            progress.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        vm.errorLive.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.trim().isEmpty())
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        vm.loadCategories();
    }

    @Override
    public void onDelete(AdminCategory c) {
        vm.deleteCategory(c);
    }
}
