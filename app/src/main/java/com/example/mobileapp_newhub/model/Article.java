package com.example.mobileapp_newhub.model;

public class Article {

    private String id;
    private String title;
    private String thumbnailUrl;
    private String content;
    private String category;
    private String publishedAt;

    public Article(String id, String title, String thumbnailUrl,
                   String content, String category, String publishedAt) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.content = content;
        this.category = category;
        this.publishedAt = publishedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public String getPublishedAt() { return publishedAt; }
}
