package com.example.mobileapp_newhub.model;

import java.io.Serializable;

public class Post implements Serializable {
    public String id;
    public String title;
    public String content;
    public String imageUrl; 
    public String categoryId;
    public long timestamp; 
    
    // Thêm field author
    public String author;

    // Additional fields for UI
    public boolean isSaved;
    public String categoryName;

    public Post() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Thêm Getter/Setter cho author
    public String getAuthor() { return author != null ? author : "Admin"; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }

    public String getCategory() { return categoryName != null ? categoryName : categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
