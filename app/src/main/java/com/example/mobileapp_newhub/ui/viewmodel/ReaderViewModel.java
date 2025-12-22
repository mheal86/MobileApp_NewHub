package com.example.mobileapp_newhub.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileapp_newhub.data.repository.OnRepositoryCallback;
import com.example.mobileapp_newhub.data.repository.Repository;
import com.example.mobileapp_newhub.data.repository.RepositoryImpl;
import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Comment;
import com.example.mobileapp_newhub.model.Post;

import java.util.ArrayList;
import java.util.List;

public class ReaderViewModel extends AndroidViewModel {

    private final Repository repository;

    private final MutableLiveData<List<Post>> allPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> savedPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> historyPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    
    // LiveData cho comments của bài viết hiện tại (Giả lập)
    private final MutableLiveData<List<Comment>> currentPostComments = new MutableLiveData<>();
    
    private final MutableLiveData<Integer> fontSize = new MutableLiveData<>(16);
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>(false);

    public ReaderViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositoryImpl(application);
        loadPosts();
        loadSavedPosts();
        loadCategories();
        loadHistoryPosts();
    }

    public void refreshPosts() {
        loadPosts();
        loadCategories();
        loadSavedPosts();
        loadHistoryPosts();
    }

    private void loadPosts() {
        repository.getPosts(true, new OnRepositoryCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                allPosts.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
                // handle error if needed
            }
        });
    }

    public void loadSavedPosts() {
        repository.getBookmarkedPosts(new OnRepositoryCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                savedPosts.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public void loadHistoryPosts() {
        repository.getHistoryPosts(new OnRepositoryCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                historyPosts.setValue(data);
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public void loadCategories() {
        repository.getCategories(true, new OnRepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }
    
    // --- COMMENT LOGIC (Giả lập) ---
    public LiveData<List<Comment>> getCurrentPostComments() {
        return currentPostComments;
    }
    
    public void loadComments(String postId) {
        // TODO: Kết nối API/Firebase thật ở đây
        // Giả lập dữ liệu
        List<Comment> mockComments = new ArrayList<>();
        // mockComments.add(new Comment("1", "User A", null, "Bài viết hay quá!", 5, System.currentTimeMillis()));
        currentPostComments.setValue(mockComments);
    }
    
    public void addComment(String postId, Comment comment) {
        // TODO: Gửi lên API/Firebase
        // Giả lập thêm vào list hiện tại
        List<Comment> current = currentPostComments.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(0, comment); // Thêm vào đầu
        currentPostComments.setValue(current);
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

    public LiveData<List<Post>> getSavedPosts() {
        return savedPosts;
    }
    
    public LiveData<List<Post>> getHistoryPosts() {
        return historyPosts;
    }

    public LiveData<List<Post>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void search(String query) {
        if (query == null || query.isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            return;
        }
        
        List<Post> currentPosts = allPosts.getValue();
        if (currentPosts != null) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : currentPosts) {
                if (p.getTitle() != null && p.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(p);
                }
            }
            searchResults.setValue(filtered);
        }
    }

    public void markPostAsViewed(Post post) {
        if (post != null && post.getId() != null) {
            repository.markViewed(post.getId());
            loadHistoryPosts();
        }
    }

    public void toggleSavePost(Post post) {
        if (post != null && post.getId() != null) {
            repository.toggleBookmark(post.getId(), new OnRepositoryCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean isSaved) {
                    post.setSaved(isSaved);
                    loadSavedPosts();
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

    public LiveData<Integer> getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        fontSize.setValue(size);
    }

    public LiveData<Boolean> isDarkMode() {
        return darkMode;
    }
    
    public void setDarkMode(boolean enabled) {
        darkMode.setValue(enabled);
    }
    
    public void toggleDarkMode() {
        Boolean current = darkMode.getValue();
        darkMode.setValue(current == null ? true : !current);
    }
}
