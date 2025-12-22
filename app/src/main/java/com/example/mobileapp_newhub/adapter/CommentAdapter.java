package com.example.mobileapp_newhub.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final Context context;
    private List<Comment> comments = new ArrayList<>();

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void setComments(List<Comment> comments) {
        this.comments = (comments != null) ? comments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtContent, txtTime;
        RatingBar ratingBar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtTime = itemView.findViewById(R.id.txtTime);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(Comment comment) {
            txtName.setText(comment.getUserName());
            txtContent.setText(comment.getContent());
            ratingBar.setRating(comment.getRating());

            // Hiển thị ảnh đại diện
            if (comment.getUserAvatar() != null && !comment.getUserAvatar().isEmpty()) {
                Glide.with(context).load(comment.getUserAvatar()).circleCrop().into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.mipmap.ic_launcher);
            }
            
            // Hiển thị thời gian (vd: "5 phút trước")
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                comment.getTimestamp(), 
                System.currentTimeMillis(), 
                DateUtils.MINUTE_IN_MILLIS
            );
            txtTime.setText(timeAgo);
        }
    }
}
