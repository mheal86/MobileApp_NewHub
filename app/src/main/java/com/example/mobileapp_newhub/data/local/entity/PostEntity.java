package com.example.mobileapp_newhub.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "posts")
public class PostEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    public String content;
    public String thumbnailUrl;
    public String categoryId;
    public long publishedAt;
}
