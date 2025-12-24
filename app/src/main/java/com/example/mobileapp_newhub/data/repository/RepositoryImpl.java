package com.example.mobileapp_newhub.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private final FirebaseAuth mAuth;

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
        this.mAuth = FirebaseAuth.getInstance();
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
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
        String userId = getCurrentUserId();
        if (posts == null || userId == null) return posts;
        for (Post p : posts) {
            if (p.id != null) {
                boolean isSaved = bookmarkDao.isBookmarked(userId, p.id) > 0;
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
        String userId = getCurrentUserId();
        executor.execute(() -> {
            Post post = Mapper.fromPostEntity(postDao.getPostById(postId));
            if (post != null && userId != null) {
                boolean isSaved = bookmarkDao.isBookmarked(userId, postId) > 0;
                post.setSaved(isSaved);
            }
            runOnMainThread(callback, post);
        });
    }

    @Override
    public LiveData<Post> getPostLive(String postId) {
        return Transformations.map(postDao.getPostByIdLive(postId), entity -> {
            if (entity == null) return null;
            return Mapper.fromPostEntity(entity);
        });
    }

    @Override
    public void toggleBookmark(String postId, OnRepositoryCallback<Boolean> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            runOnMainThreadError(callback, new Exception("User not logged in"));
            return;
        }
        executor.execute(() -> {
            boolean isCurrentlyBookmarked = bookmarkDao.isBookmarked(userId, postId) > 0;
            boolean newState;
            if (isCurrentlyBookmarked) {
                bookmarkDao.removeBookmark(userId, postId);
                newState = false;
            } else {
                BookmarkEntity b = new BookmarkEntity();
                b.userId = userId;
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
        String userId = getCurrentUserId();
        if (userId == null) {
            runOnMainThread(callback, false);
            return;
        }
        executor.execute(() -> {
            boolean isBookmarked = bookmarkDao.isBookmarked(userId, postId) > 0;
            runOnMainThread(callback, isBookmarked);
        });
    }

    @Override
    public void markViewed(String postId) {
        String userId = getCurrentUserId();
        if (userId == null) return;
        executor.execute(() -> {
            HistoryEntity h = new HistoryEntity();
            h.userId = userId;
            h.postId = postId;
            h.viewedAt = System.currentTimeMillis();
            historyDao.insert(h);
        });
    }

    @Override
    public void getBookmarkedPosts(OnRepositoryCallback<List<Post>> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            runOnMainThread(callback, new java.util.ArrayList<>());
            return;
        }
        executor.execute(() -> {
            List<String> postIds = bookmarkDao.getBookmarkPostIds(userId);
            List<Post> posts = Mapper.fromPostEntities(postDao.getPostsByIds(postIds));
            for(Post p : posts) {
                p.setSaved(true);
            }
            runOnMainThread(callback, posts);
        });
    }

    @Override
    public void getHistoryPosts(OnRepositoryCallback<List<Post>> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            runOnMainThread(callback, new java.util.ArrayList<>());
            return;
        }
        executor.execute(() -> {
            List<String> postIds = historyDao.getHistoryPostIds(userId);
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
