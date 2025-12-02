package com.example.mobileapp_newhub.data;

import com.example.mobileapp_newhub.model.Article;

import java.util.ArrayList;
import java.util.List;

public class FakeArticleDataSource {

    public static List<Article> getTopArticles() {

        List<Article> list = new ArrayList<>();

        list.add(new Article(
                "1",
                "Tin nóng: Giá vàng giảm mạnh",
                "gold",
                "Nội dung bài báo số 1...",
                "Kinh tế",
                "28/11/2025"
        ));

        list.add(new Article(
                "2",
                "Công nghệ AI bùng nổ tại Việt Nam",
                "ai",
                "Nội dung bài báo số 2 ...",
                "Công nghệ",
                "28/11/2025"
        ));

        list.add(new Article(
                "3",
                "Học Android Studio cấp tốc",
                "android",
                "Nội dung bài báo số 3 ...",
                "Giáo dục",
                "27/11/2025"
        ));

        return list;
    }
}
