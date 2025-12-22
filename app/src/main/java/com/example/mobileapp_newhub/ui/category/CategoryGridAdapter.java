package com.example.mobileapp_newhub.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.model.Category;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.VH> {

    private final List<Category> list;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryGridAdapter(List<Category> list, OnCategoryClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_grid, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCategory);
            name = itemView.findViewById(R.id.txtCategoryName);

            itemView.setOnClickListener(v -> listener.onCategoryClick(list.get(getAdapterPosition())));
        }

        void bind(Category c) {
            name.setText(c.name);
            if (c.imageUrl != null && !c.imageUrl.isEmpty()) {
                Glide.with(itemView).load(c.imageUrl).centerCrop().into(img);
            } else {
                img.setImageResource(R.color.design_default_color_secondary);
            }
        }
    }
}
