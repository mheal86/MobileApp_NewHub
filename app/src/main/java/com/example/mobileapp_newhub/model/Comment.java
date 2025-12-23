package com.example.mobileapp_newhub.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String postId; // NEW: Thêm postId
    private String userId;
    private String userName;
    private String userAvatar;
    private String content;
    private float rating; // Số sao đánh giá (1-5)
    private long timestamp;

    public Comment() { }

    public Comment(String userId, String userName, String userAvatar, String content, float rating, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // NEW: Getter/Setter cho postId
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
