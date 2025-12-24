package com.example.mobileapp_newhub.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "bookmarks", primaryKeys = {"userId", "postId"})
public class BookmarkEntity {

    @NonNull
    public String userId;

    @NonNull
    public String postId;

    public long bookmarkedAt;
}
