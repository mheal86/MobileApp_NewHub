package com.example.mobileapp_newhub.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobileapp_newhub.data.local.dao.*;
import com.example.mobileapp_newhub.data.local.entity.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                PostEntity.class,
                CategoryEntity.class,
                BookmarkEntity.class,
                HistoryEntity.class
        },
        version = 1,
        exportSchema = false // Bổ sung để tránh warning khi build
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // ExecutorService để chạy các tác vụ database trên luồng nền
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract PostDao postDao();
    public abstract CategoryDao categoryDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract HistoryDao historyDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "newshub_db"
                            )
                            // XÓA DÒNG .allowMainThreadQueries() ĐI
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
