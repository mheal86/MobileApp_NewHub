package com.example.mobileapp_newhub.data.repository;

import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Comment; // Import Comment
import com.example.mobileapp_newhub.model.Post;
import java.util.List;

public interface Repository {

    void getPosts(boolean hasNetwork, OnRepositoryCallback<List<Post>> callback);

    void getCategories(boolean hasNetwork, OnRepositoryCallback<List<Category>> callback);

    void getPostDetail(String postId, OnRepositoryCallback<Post> callback);

    void toggleBookmark(String postId, OnRepositoryCallback<Boolean> callback);

    void isBookmarked(String postId, OnRepositoryCallback<Boolean> callback);

    void markViewed(String postId);

    void getBookmarkedPosts(OnRepositoryCallback<List<Post>> callback);

    void getHistoryPosts(OnRepositoryCallback<List<Post>> callback);

    // NEW: Comments
    void getComments(String postId, OnRepositoryCallback<List<Comment>> callback);
    void addComment(String postId, Comment comment, OnRepositoryCallback<Boolean> callback);
}
