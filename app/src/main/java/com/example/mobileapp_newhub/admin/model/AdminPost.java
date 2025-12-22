package com.example.mobileapp_newhub.admin.model;

import java.io.Serializable;

public class AdminPost implements Serializable {
    public String id;
    public String title;
    public String content;
    public String categoryId;
    public String imageUrl;
    public long timestamp;

    public AdminPost() {}
}
