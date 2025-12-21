package com.example.mobileapp_newhub.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.viewmodel.ReaderViewModel;

public class SettingsFragment extends Fragment {

    private ReaderViewModel viewModel;
    private SeekBar fontSizeSeekBar;
    private TextView fontSizeValueTextView;
    private TextView previewTextView;
    private SwitchCompat darkModeSwitch;
    private Button resetButton;

    private static final int MIN_FONT_SIZE = 12;
    private static final int MAX_FONT_SIZE = 24;
    private static final int DEFAULT_FONT_SIZE = 16;

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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        setupFontSizeControl();
        setupDarkModeToggle();
        setupResetButton();
        observeSettings();

        return view;
    }

    private void initViews(View view) {
        fontSizeSeekBar = view.findViewById(R.id.seekBarFontSize);
        fontSizeValueTextView = view.findViewById(R.id.textViewFontSizeValue);
        previewTextView = view.findViewById(R.id.textViewPreview);
        darkModeSwitch = view.findViewById(R.id.switchDarkMode);
        resetButton = view.findViewById(R.id.buttonReset);
    }

    private void setupFontSizeControl() {
        fontSizeSeekBar.setMax(MAX_FONT_SIZE - MIN_FONT_SIZE);

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int fontSize = MIN_FONT_SIZE + progress;
                fontSizeValueTextView.setText(fontSize + "sp");
                previewTextView.setTextSize(fontSize);

                if (fromUser) {
                    viewModel.setFontSize(fontSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupDarkModeToggle() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setDarkMode(isChecked);
            applyDarkMode(isChecked);

            String message = isChecked ? "Đã bật chế độ tối" : "Đã tắt chế độ tối";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupResetButton() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setFontSize(DEFAULT_FONT_SIZE);
                viewModel.setDarkMode(false);
                Toast.makeText(requireContext(), "Đã đặt lại cài đặt mặc định", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeSettings() {
        viewModel.getFontSize().observe(getViewLifecycleOwner(), fontSize -> {
            if (fontSize != null) {
                fontSizeSeekBar.setProgress(fontSize - MIN_FONT_SIZE);
                fontSizeValueTextView.setText(fontSize + "sp");
                previewTextView.setTextSize(fontSize);
            }
        });

        viewModel.isDarkMode().observe(getViewLifecycleOwner(), isDarkMode -> {
            if (isDarkMode != null) {
                darkModeSwitch.setChecked(isDarkMode);
                applyDarkMode(isDarkMode);
            }
        });
    }

    private void applyDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}