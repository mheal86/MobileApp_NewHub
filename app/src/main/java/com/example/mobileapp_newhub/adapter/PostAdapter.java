package com.example.mobileapp_newhub.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public interface OnSaveClickListener {
        void onSaveClick(Post post);
    }

    private final Context context;
    private final OnPostClickListener postClickListener;
    private final OnSaveClickListener saveClickListener;
    private List<Post> posts = new ArrayList<>();
    
    private int fontSize = 16;
    private boolean isDarkMode = false;

    public PostAdapter(Context context, OnPostClickListener postClickListener, OnSaveClickListener saveClickListener) {
        this.context = context;
        this.postClickListener = postClickListener;
        this.saveClickListener = saveClickListener;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts != null ? posts : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setFontSize(int size) {
        this.fontSize = size;
        notifyDataSetChanged();
    }
    
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgThumb;
        TextView txtTitle, txtSummary;
        ImageButton btnSave;
        CardView cardView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSummary = itemView.findViewById(R.id.txtSummary);
            btnSave = itemView.findViewById(R.id.btnSave);
            cardView = (CardView) itemView;
            
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && postClickListener != null) {
                    postClickListener.onPostClick(posts.get(pos));
                }
            });

            btnSave.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && saveClickListener != null) {
                    Post post = posts.get(pos);
                    // Không tự ý thay đổi trạng thái UI ở đây, để ViewModel quyết định
                    saveClickListener.onSaveClick(post);
                }
            });
        }
        
        void updateSaveIcon(boolean isSaved) {
            if (isSaved) {
                btnSave.setImageResource(android.R.drawable.star_on);
            } else {
                btnSave.setImageResource(android.R.drawable.star_off);
            }
        }

        void bind(Post post) {
            txtTitle.setText(post.getTitle());
            txtSummary.setText(post.getContent()); 
            
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            txtSummary.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
            
            if (isDarkMode) {
                cardView.setCardBackgroundColor(Color.DKGRAY);
                txtTitle.setTextColor(Color.WHITE);
                txtSummary.setTextColor(Color.LTGRAY);
                btnSave.setColorFilter(Color.WHITE);
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
                txtTitle.setTextColor(Color.BLACK);
                txtSummary.setTextColor(Color.DKGRAY);
                btnSave.clearColorFilter();
            }

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(post.getImageUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(imgThumb);
            } else {
                imgThumb.setImageResource(R.mipmap.ic_launcher);
            }

            updateSaveIcon(post.isSaved());
        }
    }
}
