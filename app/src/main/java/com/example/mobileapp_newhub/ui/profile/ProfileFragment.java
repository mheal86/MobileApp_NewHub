package com.example.mobileapp_newhub.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.auth.AuthViewModel;
import com.example.mobileapp_newhub.auth.LoginActivity;
import com.example.mobileapp_newhub.model.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private AuthViewModel authViewModel;

    // UI Layouts
    private NestedScrollView profileLayout;
    private LinearLayout guestLayout;

    // UI Elements for Profile
    private ImageView avatarImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private ChipGroup interestsChipGroup;
    
    // Buttons / Clickable Layouts
    private View uploadAvatarButton; 
    private View editProfileRow;
    private View changePasswordRow;
    private View logoutRow;

    // UI Elements for Guest
    private Button loginNavButton;
    
    // Dialog UI reference (to update image inside dialog when picked)
    private ImageView dialogAvatarImageView;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // Update main UI
                    authViewModel.uploadAvatar(uri);
                    
                    // Update Dialog UI if open
                    if (dialogAvatarImageView != null) {
                        Glide.with(requireContext())
                                .load(uri)
                                .circleCrop()
                                .into(dialogAvatarImageView);
                    }
                    
                    Toast.makeText(requireContext(), "Đang cập nhật ảnh...", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Init Layouts
        profileLayout = view.findViewById(R.id.profileLayout);
        guestLayout = view.findViewById(R.id.guestLayout);

        // Init Profile Views
        avatarImageView = view.findViewById(R.id.avatarImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        interestsChipGroup = view.findViewById(R.id.interestsChipGroup);
        
        uploadAvatarButton = view.findViewById(R.id.uploadAvatarButton);
        editProfileRow = view.findViewById(R.id.editProfileRow);
        changePasswordRow = view.findViewById(R.id.changePasswordRow);
        logoutRow = view.findViewById(R.id.logoutRow);

        // Init Guest Views
        loginNavButton = view.findViewById(R.id.loginNavButton);

        // Kiểm tra trạng thái đăng nhập ban đầu
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUIState(currentUser);

        // Lắng nghe thay đổi User
        authViewModel.getUserLiveData().observe(getViewLifecycleOwner(), this::updateUIState);

        // Lắng nghe thông tin Profile
        authViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                updateProfileUI(user);
            }
        });

        authViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
             if (error != null) {
                 Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
             }
        });

        // Xử lý sự kiện Click
        setupClickListeners();
    }

    private void setupClickListeners() {
        logoutRow.setOnClickListener(v -> authViewModel.logout());

        loginNavButton.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        uploadAvatarButton.setOnClickListener(v -> launchPhotoPicker());

        editProfileRow.setOnClickListener(v -> showEditProfileDialog());

        changePasswordRow.setOnClickListener(v -> showChangePasswordDialog());
    }
    
    private void launchPhotoPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void updateUIState(FirebaseUser user) {
        if (user != null) {
            profileLayout.setVisibility(View.VISIBLE);
            guestLayout.setVisibility(View.GONE);
        } else {
            profileLayout.setVisibility(View.GONE);
            guestLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateProfileUI(User user) {
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(user.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).circleCrop().into(avatarImageView);
        } else {
             avatarImageView.setImageResource(R.mipmap.ic_launcher);
        }
        
        // Update Interests Chips
        if (interestsChipGroup != null) {
            interestsChipGroup.removeAllViews();
            if (user.getInterests() != null) {
                for (String interest : user.getInterests()) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(interest);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    interestsChipGroup.addView(chip);
                }
            }
        }
    }

    private void showEditProfileDialog() {
        // Create custom layout for dialog
        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        scrollView.addView(layout);
        
        User currentUser = authViewModel.getUserProfileLiveData().getValue();

        // 0. AVATAR EDIT SECTION (TOP)
        dialogAvatarImageView = new ImageView(requireContext());
        int size = (int) (100 * getResources().getDisplayMetrics().density); // 100dp size
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(size, size);
        imageParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageParams.bottomMargin = (int) (16 * getResources().getDisplayMetrics().density);
        dialogAvatarImageView.setLayoutParams(imageParams);
        dialogAvatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        // Load current avatar
        if (currentUser != null && currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
            Glide.with(requireContext()).load(currentUser.getPhotoUrl()).circleCrop().into(dialogAvatarImageView);
        } else {
            dialogAvatarImageView.setImageResource(R.mipmap.ic_launcher);
        }
        
        // Add click listener to change avatar
        dialogAvatarImageView.setOnClickListener(v -> launchPhotoPicker());
        
        layout.addView(dialogAvatarImageView);
        
        TextView changeAvatarHint = new TextView(requireContext());
        changeAvatarHint.setText("Chạm vào ảnh để thay đổi");
        changeAvatarHint.setTextSize(12);
        changeAvatarHint.setGravity(Gravity.CENTER_HORIZONTAL);
        changeAvatarHint.setPadding(0, 0, 0, 30);
        layout.addView(changeAvatarHint);


        // 1. Name Input
        final EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Tên hiển thị");
        nameInput.setText(nameTextView.getText());
        layout.addView(nameInput);

        // 2. Label
        TextView interestsLabel = new TextView(requireContext());
        interestsLabel.setText("Chọn sở thích:");
        interestsLabel.setPadding(0, 30, 0, 10);
        interestsLabel.setTextSize(16);
        interestsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(interestsLabel);

        // 3. Checkboxes
        final String[] allInterests = {"Công nghệ", "Kinh tế", "AI", "Thể thao", "Văn hóa", "Du lịch", "Ẩm thực"};
        List<CheckBox> checkBoxes = new ArrayList<>();
        
        List<String> currentInterests = (currentUser != null && currentUser.getInterests() != null) 
                                        ? currentUser.getInterests() : new ArrayList<>();

        for (String interest : allInterests) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(interest);
            if (currentInterests.contains(interest)) {
                checkBox.setChecked(true);
            }
            layout.addView(checkBox);
            checkBoxes.add(checkBox);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Chỉnh sửa thông tin")
                .setView(scrollView)
                .setPositiveButton("Lưu", (d, which) -> {
                    String newName = nameInput.getText().toString();
                    List<String> selectedInterests = new ArrayList<>();
                    for (CheckBox cb : checkBoxes) {
                        if (cb.isChecked()) {
                            selectedInterests.add(cb.getText().toString());
                        }
                    }
                    if (!newName.isEmpty()) {
                        authViewModel.updateUserProfile(newName, selectedInterests);
                    }
                })
                .setNegativeButton("Hủy", null)
                .create();
        
        // Clean up reference when dialog closes
        dialog.setOnDismissListener(d -> dialogAvatarImageView = null);
        
        dialog.show();
    }

    private void showChangePasswordDialog() {
        EditText newPasswordInput = new EditText(requireContext());
        newPasswordInput.setHint("Nhập mật khẩu mới");
        newPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(requireContext())
                .setTitle("Đổi mật khẩu")
                .setView(newPasswordInput)
                .setPositiveButton("Đổi", (dialog, which) -> {
                    String newPassword = newPasswordInput.getText().toString();
                    if (newPassword.length() >= 6) {
                        authViewModel.changePassword(newPassword);
                    } else {
                        Toast.makeText(requireContext(), "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}