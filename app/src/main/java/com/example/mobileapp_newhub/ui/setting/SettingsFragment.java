package com.example.mobileapp_newhub.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileapp_newhub.R;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Switch switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Demo xử lý bật/tắt Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Dark mode bật", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Dark mode tắt", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
