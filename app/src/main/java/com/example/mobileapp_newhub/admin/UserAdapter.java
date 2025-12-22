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
import com.example.mobileapp_newhub.admin.model.AdminUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    private final List<AdminUser> list;
    private final Listener listener;

    public interface Listener {
        void onDeleteUser(AdminUser user);
    }

    public UserAdapter(List<AdminUser> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
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
        TextView name, email, role;
        ImageButton btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgAvatar);
            name = itemView.findViewById(R.id.txtName);
            email = itemView.findViewById(R.id.txtEmail);
            role = itemView.findViewById(R.id.txtRole);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(v -> listener.onDeleteUser(list.get(getAdapterPosition())));
        }

        void bind(AdminUser u) {
            name.setText(u.name != null ? u.name : "No Name");
            email.setText(u.email != null ? u.email : "");
            role.setText(u.role != null ? u.role : "user");
            
            if (u.photoUrl != null && !u.photoUrl.isEmpty()) {
                Glide.with(itemView).load(u.photoUrl).circleCrop().into(img);
            } else {
                img.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }
}
