package com.example.mobileapp_newhub.data.repository;

import com.example.mobileapp_newhub.model.Category;import com.example.mobileapp_newhub.model.Post;
import java.util.List;

public interface Repository {

    void getPosts(boolean hasNetwork, OnRepositoryCallback<List<Post>> callback);

    void getCategories(boolean hasNetwork, OnRepositoryCallback<List<Category>> callback);

    // SỬA: Thêm callback
    void getPostDetail(String postId, OnRepositoryCallback<Post> callback);

    // SỬA: Thêm callback
    void toggleBookmark(String postId, OnRepositoryCallback<Boolean> callback);

    // SỬA: Thêm callback
    void isBookmarked(String postId, OnRepositoryCallback<Boolean> callback);

    void markViewed(String postId);

    // SỬA: Thêm callback
    void getBookmarkedPosts(OnRepositoryCallback<List<Post>> callback);

    // SỬA: Thêm callback
    void getHistoryPosts(OnRepositoryCallback<List<Post>> callback);
}
