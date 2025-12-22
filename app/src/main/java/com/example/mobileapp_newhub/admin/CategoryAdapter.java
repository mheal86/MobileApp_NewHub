package com.example.mobileapp_newhub.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    private final List<AdminCategory> list;
    private final Listener listener;

    public interface Listener {
        void onDelete(AdminCategory c);
    }

    public CategoryAdapter(List<AdminCategory> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạm dùng item_admin_user layout hoặc tạo mới item_admin_category.xml
        // Tạo mới cho rõ ràng
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category, parent, false);
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
        TextView name;
        ImageButton btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(v -> listener.onDelete(list.get(getAdapterPosition())));
        }

        void bind(AdminCategory c) {
            name.setText(c.name);
        }
    }
}
