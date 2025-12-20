package com.example.mobileapp_newhub.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.data.FakeArticleDataSource;
import com.example.mobileapp_newhub.ui.adapter.ArticleAdapter;

public class SearchFragment extends Fragment {

    private EditText edtSearch;
    private RecyclerView rvSearchResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        rvSearchResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // Demo: hiển thị danh sách bài viết (fake data)
        ArticleAdapter adapter = new ArticleAdapter(
                requireContext(),
                FakeArticleDataSource.getTopArticles()
        );

        rvSearchResults.setAdapter(adapter);

        return view;
    }
}
