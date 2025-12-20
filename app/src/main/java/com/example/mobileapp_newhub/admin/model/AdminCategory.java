package com.example.mobileapp_newhub.admin.model;

import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class AdminCategory {
    public String id;
    public String name;
    public Timestamp createdAt;

    public AdminCategory() {}

    public Map<String, Object> toMap(boolean isCreate) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        if (isCreate) m.put("createdAt", Timestamp.now());
        return m;
    }
}
