package com.example.mobileapp_newhub.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mobileapp_newhub.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ReaderViewModel extends ViewModel {

    // ==================== DATA ====================

    private final MutableLiveData<List<Article>> allArticles = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> savedArticles = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> viewedArticles = new MutableLiveData<>();

    // ==================== FILTER & SEARCH ====================

    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    private final LiveData<List<Article>> categoryArticles =
            Transformations.switchMap(selectedCategory, category -> {
                MutableLiveData<List<Article>> result = new MutableLiveData<>();
                List<Article> source = allArticles.getValue();

                if (source == null) {
                    result.setValue(new ArrayList<>());
                    return result;
                }

                if (category == null || category.isEmpty()) {
                    result.setValue(source);
                } else {
                    List<Article> filtered = new ArrayList<>();
                    for (Article a : source) {
                        if (category.equals(a.getCategory())) {
                            filtered.add(a);
                        }
                    }
                    result.setValue(filtered);
                }
                return result;
            });

    private final LiveData<List<Article>> searchResults =
            Transformations.switchMap(searchQuery, query -> {
                MutableLiveData<List<Article>> result = new MutableLiveData<>();
                List<Article> source = allArticles.getValue();

                if (source == null || query == null || query.isEmpty()) {
                    result.setValue(new ArrayList<>());
                    return result;
                }

                List<Article> filtered = new ArrayList<>();
                for (Article a : source) {
                    if (a.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(a);
                    }
                }
                result.setValue(filtered);
                return result;
            });

    // ==================== SETTINGS ====================

    private final MutableLiveData<Integer> fontSize = new MutableLiveData<>(16);
    private final MutableLiveData<Boolean> darkMode = new MutableLiveData<>(false);

    // ==================== PUBLIC METHODS ====================

    public void setArticles(List<Article> articles) {
        allArticles.setValue(articles);
    }

    public LiveData<List<Article>> getAllArticles() {
        return allArticles;
    }

    public LiveData<List<Article>> getCategoryArticles() {
        return categoryArticles;
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
    }

    public LiveData<List<Article>> getSearchResults() {
        return searchResults;
    }

    public void search(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<Integer> getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        if (size >= 12 && size <= 24) {
            fontSize.setValue(size);
        }
    }

    public LiveData<Boolean> isDarkMode() {
        return darkMode;
    }

    public void toggleDarkMode() {
        Boolean current = darkMode.getValue();
        darkMode.setValue(current == null || !current);
    }
}
