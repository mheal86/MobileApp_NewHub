package com.example.mobileapp_newhub.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp_newhub.data.local.entity.BookmarkEntity;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE userId = :userId")
    List<BookmarkEntity> getBookmarks(String userId);

    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId AND postId = :postId")
    int isBookmarked(String userId, String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bookmark(BookmarkEntity entity);

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND postId = :postId")
    void removeBookmark(String userId, String postId);

    @Query("SELECT postId FROM bookmarks WHERE userId = :userId ORDER BY bookmarkedAt DESC")
    List<String> getBookmarkPostIds(String userId);
    
    @Query("DELETE FROM bookmarks WHERE userId = :userId")
    void clearUserBookmarks(String userId);
}
