package com.example.mobileapp_newhub.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "history", primaryKeys = {"userId", "postId"})
public class HistoryEntity {

    @NonNull
    public String userId;

    @NonNull
    public String postId;

    public long viewedAt;
}
