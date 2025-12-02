package com.example.mobileapp_newhub.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.data.FakeArticleDataSource;
import com.example.mobileapp_newhub.ui.adapter.ArticleAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView rvArticles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rvArticles = v.findViewById(R.id.rvArticles);
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));

        ArticleAdapter adapter =
                new ArticleAdapter(getContext(), FakeArticleDataSource.getTopArticles());

        rvArticles.setAdapter(adapter);

        return v;
    }
}
