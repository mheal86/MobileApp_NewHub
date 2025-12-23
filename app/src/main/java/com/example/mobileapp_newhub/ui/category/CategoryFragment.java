package com.example.mobileapp_newhub.ui.category;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
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
import com.example.mobileapp_newhub.utils.NetworkUtils;

public class CategoryFragment extends Fragment {

    private ReaderViewModel viewModel;
    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private TextView emptyView;

    // Biến để quản lý lắng nghe mạng
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

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

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));

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
        
        // Load lần đầu tiên
        viewModel.loadCategories(NetworkUtils.isNetworkAvailable(requireContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkCallback();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterNetworkCallback();
    }

    // Đăng ký lắng nghe sự kiện mạng
    private void registerNetworkCallback() {
        if (getContext() == null) return;
        
        // Chỉ hỗ trợ từ Android N (API 24) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        // Khi có mạng -> Load lại dữ liệu mới nhất (true)
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                viewModel.loadCategories(true);
                            });
                        }
                    }
                };
                try {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Hủy đăng ký để tránh lỗi
    private void unregisterNetworkCallback() {
        if (connectivityManager != null && networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        try {
             Navigation.findNavController(requireView()).navigate(R.id.action_categoryFragment_to_categoryPostsFragment, bundle);
        } catch (Exception e) {
             Toast.makeText(requireContext(), "Chưa cài đặt action navigation", Toast.LENGTH_SHORT).show();
        }
    }
}
