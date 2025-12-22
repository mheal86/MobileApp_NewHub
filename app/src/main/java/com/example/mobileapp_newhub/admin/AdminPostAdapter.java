package com.example.mobileapp_newhub.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp_newhub.R;
import com.example.mobileapp_newhub.admin.model.AdminPost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.VH> {

    private final List<AdminPost> list;
    private final Listener listener;

    public interface Listener {
        void onEdit(AdminPost post);
        void onDelete(AdminPost post);
    }

    public AdminPostAdapter(List<AdminPost> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạm dùng layout post thường nếu chưa có layout admin riêng, 
        // hoặc tạo item_admin_post.xml
        // Ở đây giả định ta sẽ tạo item_admin_post.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AdminPost p = list.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, date;
        ImageButton btnEdit, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgThumb);
            title = itemView.findViewById(R.id.txtTitle);
            date = itemView.findViewById(R.id.txtDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> listener.onEdit(list.get(getAdapterPosition())));
            btnDelete.setOnClickListener(v -> listener.onDelete(list.get(getAdapterPosition())));
        }

        void bind(AdminPost p) {
            title.setText(p.title);
            if (p.timestamp > 0) {
                date.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(p.timestamp)));
            } else {
                date.setText("");
            }
            if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
                Glide.with(itemView).load(p.imageUrl).centerCrop().into(img);
            } else {
                img.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}
