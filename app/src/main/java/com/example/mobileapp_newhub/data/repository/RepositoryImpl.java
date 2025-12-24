package com.example.mobileapp_newhub.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData; // Import LiveData
import androidx.lifecycle.Transformations; // Import Transformations

import com.example.mobileapp_newhub.data.local.AppDatabase;
import com.example.mobileapp_newhub.data.local.dao.*;
import com.example.mobileapp_newhub.data.local.entity.BookmarkEntity;
import com.example.mobileapp_newhub.data.local.entity.HistoryEntity;
import com.example.mobileapp_newhub.data.remote.FirestoreDataSource;
import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Comment;
import com.example.mobileapp_newhub.model.Post;
import java.util.List;
import java.util.concurrent.Executor;

public class RepositoryImpl implements Repository {

    private final FirestoreDataSource remote;
    private final PostDao postDao;
    private final CategoryDao categoryDao;
    private final BookmarkDao bookmarkDao;
    private final HistoryDao historyDao;

    private final Executor executor;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public RepositoryImpl(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.executor = AppDatabase.databaseWriteExecutor;
        this.remote = new FirestoreDataSource();
        this.postDao = db.postDao();
        this.categoryDao = db.categoryDao();
        this.bookmarkDao = db.bookmarkDao();
        this.historyDao = db.historyDao();
    }

    private <T> void runOnMainThread(OnRepositoryCallback<T> callback, T data) {
        if (callback != null) {
            mainThreadHandler.post(() -> callback.onSuccess(data));
        }
    }

    private <T> void runOnMainThreadError(OnRepositoryCallback<T> callback, Exception e) {
        if (callback != null) {
            mainThreadHandler.post(() -> callback.onFailure(e));
        }
    }

    private List<Post> checkBookmarkStatus(List<Post> posts) {
        if (posts == null) return null;
        for (Post p : posts) {
            if (p.id != null) {
                boolean isSaved = bookmarkDao.isBookmarked(p.id) > 0;
                p.setSaved(isSaved);
            }
        }
        return posts;
    }

    @Override
    public void getPosts(boolean hasNetwork, OnRepositoryCallback<List<Post>> callback) {
        if (hasNetwork) {
            remote.fetchPosts(
                    posts -> executor.execute(() -> {
                        postDao.insertAll(Mapper.toPostEntities(posts));
                        List<Post> checkedPosts = checkBookmarkStatus(posts);
                        runOnMainThread(callback, checkedPosts);
                    }),
                    e -> executor.execute(() -> {
                        List<Post> postsFromDb = Mapper.fromPostEntities(postDao.getPosts());
                        List<Post> checkedPosts = checkBookmarkStatus(postsFromDb);
                        runOnMainThread(callback, checkedPosts);
                    })
            );
        } else {
            executor.execute(() -> {
                List<Post> postsFromDb = Mapper.fromPostEntities(postDao.getPosts());
                List<Post> checkedPosts = checkBookmarkStatus(postsFromDb);
                runOnMainThread(callback, checkedPosts);
            });
        }
    }

    @Override
    public void getCategories(boolean hasNetwork, OnRepositoryCallback<List<Category>> callback) {
        if (hasNetwork) {
            remote.fetchCategories(
                    categories -> executor.execute(() -> {
                        categoryDao.insertAll(Mapper.toCategoryEntities(categories));
                        runOnMainThread(callback, categories);
                    }),
                    e -> executor.execute(() -> {
                        List<Category> categoriesFromDb = Mapper.fromCategoryEntities(categoryDao.getAll());
                        runOnMainThread(callback, categoriesFromDb);
                    })
            );
        } else {
            executor.execute(() -> {
                List<Category> categoriesFromDb = Mapper.fromCategoryEntities(categoryDao.getAll());
                runOnMainThread(callback, categoriesFromDb);
            });
        }
    }

    @Override
    public void getPostDetail(String postId, OnRepositoryCallback<Post> callback) {
        executor.execute(() -> {
            Post post = Mapper.fromPostEntity(postDao.getPostById(postId));
            if (post != null) {
                boolean isSaved = bookmarkDao.isBookmarked(postId) > 0;
                post.setSaved(isSaved);
            }
            runOnMainThread(callback, post);
        });
    }

    // NEW: Implement Realtime Post Detail
    @Override
    public LiveData<Post> getPostLive(String postId) {
        return Transformations.map(postDao.getPostByIdLive(postId), entity -> {
            if (entity == null) return null;
            return Mapper.fromPostEntity(entity);
        });
    }

    @Override
    public void toggleBookmark(String postId, OnRepositoryCallback<Boolean> callback) {
        executor.execute(() -> {
            boolean isCurrentlyBookmarked = bookmarkDao.isBookmarked(postId) > 0;
            boolean newState;
            
            if (isCurrentlyBookmarked) {
                bookmarkDao.removeBookmark(postId);
                newState = false;
            } else {
                BookmarkEntity b = new BookmarkEntity();
                b.postId = postId;
                b.bookmarkedAt = System.currentTimeMillis();
                bookmarkDao.bookmark(b);
                newState = true;
            }
            runOnMainThread(callback, newState); 
        });
    }

    @Override
    public void isBookmarked(String postId, OnRepositoryCallback<Boolean> callback) {
        executor.execute(() -> {
            boolean isBookmarked = bookmarkDao.isBookmarked(postId) > 0;
            runOnMainThread(callback, isBookmarked);
        });
    }

    @Override
    public void markViewed(String postId) {
        executor.execute(() -> {
            HistoryEntity h = new HistoryEntity();
            h.postId = postId;
            h.viewedAt = System.currentTimeMillis();
            historyDao.insert(h);
        });
    }

    @Override
    public void getBookmarkedPosts(OnRepositoryCallback<List<Post>> callback) {
        executor.execute(() -> {
            List<String> postIds = bookmarkDao.getBookmarkPostIds();
            List<Post> posts = Mapper.fromPostEntities(postDao.getPostsByIds(postIds));
            for(Post p : posts) {
                p.setSaved(true);
            }
            runOnMainThread(callback, posts);
        });
    }

    @Override
    public void getHistoryPosts(OnRepositoryCallback<List<Post>> callback) {
        executor.execute(() -> {
            List<String> postIds = historyDao.getHistoryPostIds();
            List<Post> posts = Mapper.fromPostEntities(postDao.getPostsByIds(postIds));
            List<Post> checkedPosts = checkBookmarkStatus(posts);
            runOnMainThread(callback, checkedPosts);
        });
    }

    @Override
    public void getComments(String postId, OnRepositoryCallback<List<Comment>> callback) {
        remote.fetchComments(
            postId,
            comments -> runOnMainThread(callback, comments),
            e -> runOnMainThreadError(callback, e)
        );
    }

    @Override
    public void addComment(String postId, Comment comment, OnRepositoryCallback<Boolean> callback) {
        comment.setPostId(postId); 
        remote.addComment(
            comment,
            v -> runOnMainThread(callback, true),
            e -> runOnMainThreadError(callback, e)
        );
    }
}
