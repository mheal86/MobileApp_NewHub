package com.example.mobileapp_newhub.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class HistoryEntity {

    @PrimaryKey
    @NonNull
    public String postId;

    public long viewedAt;
}
