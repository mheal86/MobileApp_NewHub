package com.example.mobileapp_newhub.admin.model;

import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class AdminPost {
    public String id;
    public String title;
    public String summary;
    public String content;

    public String categoryId;
    public String categoryName;

    public String imageUrl;   // download URL
    public String imagePath;  // storage path to delete

    public String authorId;
    public Timestamp createdAt;
    public Timestamp updatedAt;

    public AdminPost() {}

    public Map<String, Object> toMap(boolean isCreate) {
        Map<String, Object> m = new HashMap<>();
        m.put("title", title);
        m.put("summary", summary);
        m.put("content", content);
        m.put("categoryId", categoryId);
        m.put("categoryName", categoryName);
        m.put("imageUrl", imageUrl);
        m.put("imagePath", imagePath);
        m.put("authorId", authorId);
        if (isCreate) m.put("createdAt", Timestamp.now());
        m.put("updatedAt", Timestamp.now());
        return m;
    }
}
