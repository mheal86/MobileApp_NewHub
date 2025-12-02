package com.example.mobileapp_newhub.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Article;
import com.example.mobileapp_newhub.ui.detail.ArticleDetailActivity;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {

        Article a = articles.get(position);

        holder.txtTitle.setText(a.getTitle());
        holder.txtCategory.setText(a.getCategory());
        holder.txtTime.setText(a.getPublishedAt());

        // ⭐ LOAD ẢNH TRONG DRAWABLE
        if (a.getThumbnailUrl() != null) {
            int resId = context.getResources().getIdentifier(
                    a.getThumbnailUrl(),   // tên file không có .png
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                holder.imgThumb.setImageResource(resId);
            } else {
                holder.imgThumb.setImageResource(R.mipmap.ic_launcher); // fallback
            }
        }

        // ⭐ SỰ KIỆN CLICK MỞ MÀN CHI TIẾT
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("title", a.getTitle());
            intent.putExtra("content", a.getContent());
            intent.putExtra("category", a.getCategory());
            intent.putExtra("time", a.getPublishedAt());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumb;
        TextView txtTitle, txtCategory, txtTime;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
