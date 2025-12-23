package com.example.mobileapp_newhub.utils;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {

    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized) return;

        Map<String, String> config = new HashMap<>();
        // Hãy thay thế "your_cloud_name" bằng Cloud Name của bạn
        config.put("cloud_name", "djairqbfj");
        config.put("api_key", "178768548981231");
        config.put("api_secret", "3lvSqHEwSBDU74NhxUVhPgtiloI");
        // config.put("secure", "true"); // Tùy chọn: sử dụng HTTPS

        try {
            MediaManager.init(context, config);
            isInitialized = true;
        } catch (IllegalStateException e) {
            // Đã khởi tạo rồi thì bỏ qua
            isInitialized = true;
        }
    }
}
