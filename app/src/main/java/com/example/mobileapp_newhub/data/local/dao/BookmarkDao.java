package com.example.mobileapp_newhub.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp_newhub.data.local.entity.BookmarkEntity;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Query("SELECT * FROM bookmarks")
    List<BookmarkEntity> getBookmarks();

    @Query("SELECT COUNT(*) FROM bookmarks WHERE postId = :postId")
    int isBookmarked(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bookmark(BookmarkEntity entity);

    @Query("DELETE FROM bookmarks WHERE postId = :postId")
    void removeBookmark(String postId);

    /**
     * Lấy danh sách ID của các bài viết đã được bookmark.
     */
    @Query("SELECT postId FROM bookmarks ORDER BY bookmarkedAt DESC")
    List<String> getBookmarkPostIds();
}
