package com.example.mobileapp_newhub.data.local.dao;

import androidx.lifecycle.LiveData; // Import LiveData
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp_newhub.data.local.entity.PostEntity;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts ORDER BY publishedAt DESC")
    List<PostEntity> getPosts();

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    PostEntity getPostById(String id);
    
    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    LiveData<PostEntity> getPostByIdLive(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PostEntity> posts);

    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    List<PostEntity> getPostsByIds(List<String> postIds);
}
