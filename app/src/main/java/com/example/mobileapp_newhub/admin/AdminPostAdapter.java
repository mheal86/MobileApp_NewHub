package com.example.mobileapp_newhub.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminPost;

import java.util.List;

public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.VH> {

    public interface Listener {
        void onEdit(AdminPost post);
        void onDelete(AdminPost post);
    }

    private final List<AdminPost> items;
    private final Listener listener;

    public AdminPostAdapter(List<AdminPost> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminPost p = items.get(position);

        h.tvTitle.setText(p.title == null ? "(no title)" : p.title);

        String meta = "";
        if (p.categoryName != null) meta += p.categoryName;
        if (p.summary != null && !p.summary.trim().isEmpty()) meta += " • " + p.summary;
        h.tvMeta.setText(meta);

        h.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;
        ImageButton btnEdit, btnDelete;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
