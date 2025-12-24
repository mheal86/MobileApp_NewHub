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
import com.example.mobileapp_newhub.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ReaderViewModel extends AndroidViewModel {

    private final Repository repository;
    private final FirebaseAuth mAuth;

    private final MutableLiveData<List<Post>> allPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> savedPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> historyPosts = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    
    private final MutableLiveData<List<Comment>> currentPostComments = new MutableLiveData<>();
    
    private final MutableLiveData<Integer> fontSize = new MutableLiveData<>(16);
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>(false);
    
    private final MutableLiveData<Boolean> isOfflineMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ReaderViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositoryImpl(application);
        mAuth = FirebaseAuth.getInstance();
        
        checkNetworkAndLoad();
    }
    
    private void checkNetworkAndLoad() {
        boolean hasNetwork = NetworkUtils.isNetworkAvailable(getApplication());
        isOfflineMode.setValue(!hasNetwork);
        
        loadPosts(hasNetwork);
        loadCategories(hasNetwork);
        loadSavedPosts();
        loadHistoryPosts();
    }

    public void refreshPosts() {
        checkNetworkAndLoad();
    }
    
    public LiveData<Boolean> getIsOfflineMode() {
        return isOfflineMode;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    private void loadPosts(boolean hasNetwork) {
        repository.getPosts(hasNetwork, new OnRepositoryCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                allPosts.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
                errorLiveData.setValue("Lỗi tải bài viết: " + e.getMessage());
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

    public void loadCategories(boolean hasNetwork) {
        repository.getCategories(hasNetwork, new OnRepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categories.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    
    // NEW: Expose LiveData for DetailFragment
    public LiveData<Post> getPostDetailLive(String postId) {
        return repository.getPostLive(postId);
    }
    
    public LiveData<List<Comment>> getCurrentPostComments() {
        return currentPostComments;
    }
    
    public void loadComments(String postId) {
        if (postId == null) return;
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
             currentPostComments.setValue(new ArrayList<>());
             return;
        }

        repository.getComments(postId, new OnRepositoryCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                currentPostComments.setValue(data);
            }

            @Override
            public void onFailure(Exception e) {
                currentPostComments.setValue(new ArrayList<>());
            }
        });
    }
    
    public void addComment(String postId, Comment comment, OnRepositoryCallback<Boolean> callback) {
        if (mAuth.getCurrentUser() == null) {
            if(callback != null) callback.onFailure(new Exception("Bạn cần đăng nhập để bình luận"));
            return;
        }

        if (postId == null || comment == null) {
            if(callback != null) callback.onFailure(new Exception("Dữ liệu không hợp lệ"));
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            if(callback != null) callback.onFailure(new Exception("Không có kết nối mạng!"));
            return;
        }
        
        repository.addComment(postId, comment, new OnRepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if(callback != null) callback.onSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                if(callback != null) callback.onFailure(e);
            }
        });
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
        if (mAuth.getCurrentUser() == null) {
            errorLiveData.setValue("Vui lòng đăng nhập để sử dụng tính năng yêu thích!");
            return;
        }

        if (post != null && post.getId() != null) {
            repository.toggleBookmark(post.getId(), new OnRepositoryCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean isSaved) {
                    post.setSaved(isSaved);
                    List<Post> currentList = allPosts.getValue();
                    if (currentList != null) {
                        for (Post p : currentList) {
                            if (p.getId().equals(post.getId())) {
                                p.setSaved(isSaved);
                                break;
                            }
                        }
                        allPosts.setValue(currentList);
                    }
                    loadSavedPosts();
                }

                @Override
                public void onFailure(Exception e) {
                    errorLiveData.setValue("Lỗi khi lưu bài viết");
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

    public void clearUserData() {
        savedPosts.setValue(new ArrayList<>());
        historyPosts.setValue(new ArrayList<>());
    }
}
