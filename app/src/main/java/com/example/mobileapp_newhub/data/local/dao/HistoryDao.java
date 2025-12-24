package com.example.mobileapp_newhub.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobileapp_newhub.data.local.entity.HistoryEntity;

import java.util.List;

@Dao
public interface HistoryDao {

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY viewedAt DESC")
    List<HistoryEntity> getHistory(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistoryEntity entity);

    /**
     * Lấy danh sách ID của các bài viết đã xem.
     */
    @Query("SELECT postId FROM history WHERE userId = :userId ORDER BY viewedAt DESC")
    List<String> getHistoryPostIds(String userId);
    
    @Query("DELETE FROM history WHERE userId = :userId")
    void clearUserHistory(String userId);
}
