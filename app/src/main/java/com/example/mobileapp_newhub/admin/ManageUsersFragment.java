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

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminUser;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersFragment extends Fragment implements UserAdapter.Listener {

    private AdminViewModel vm;
    private RecyclerView rv;
    private ProgressBar progress;

    private final List<AdminUser> items = new ArrayList<>();
    private UserAdapter adapter;

    public ManageUsersFragment() {
        super(R.layout.fragment_manage_users);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(AdminViewModel.class);

        rv = view.findViewById(R.id.rvUsers);
        progress = view.findViewById(R.id.progress);

        adapter = new UserAdapter(items, this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        vm.usersLive.observe(getViewLifecycleOwner(), list -> {
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

        vm.loadUsers();
    }

    @Override
    public void onDeleteUser(AdminUser user) {
        // SỬA: Kiểm tra nếu là Admin thì chặn xóa ngay lập tức
        if ("admin".equalsIgnoreCase(user.role)) {
            Toast.makeText(requireContext(), "Không thể xóa tài khoản Admin!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá người dùng?")
                .setMessage("Bạn có chắc muốn xoá tài khoản này không?")
                .setNegativeButton("Huỷ", null)
                .setPositiveButton("Xoá", (d, w) -> vm.deleteUser(user))
                .show();
    }

    @Override
    public void onUserClick(AdminUser user) {
        Intent intent = new Intent(requireContext(), AdminUserDetailActivity.class);
        intent.putExtra("id", user.id);
        intent.putExtra("name", user.name);
        intent.putExtra("email", user.email);
        intent.putExtra("role", user.role);
        intent.putExtra("photoUrl", user.photoUrl);
        startActivity(intent);
    }
}
