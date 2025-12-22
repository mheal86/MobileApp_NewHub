package com.example.mobileapp_newhub.data.repository;

import com.example.mobileapp_newhub.data.local.entity.CategoryEntity;
import com.example.mobileapp_newhub.data.local.entity.PostEntity;
import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Post;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static List<PostEntity> toPostEntities(List<Post> posts) {
        List<PostEntity> list = new ArrayList<>();
        if (posts == null) return list;
        for (Post p : posts) {
            PostEntity e = new PostEntity();
            e.id = p.id;
            e.title = p.title;
            e.content = p.content;
            // Map từ field mới của Post sang Entity
            // Post dùng 'imageUrl', Entity dùng 'thumbnailUrl' (nếu Entity chưa sửa)
            // Để an toàn, giả sử Entity vẫn dùng tên cũ, còn Post đã sửa thành imageUrl
            e.thumbnailUrl = p.imageUrl; 
            
            e.categoryId = p.categoryId;
            // Post dùng 'timestamp', Entity dùng 'publishedAt'
            e.publishedAt = p.timestamp;
            list.add(e);
        }
        return list;
    }

    public static List<Post> fromPostEntities(List<PostEntity> entities) {
        List<Post> list = new ArrayList<>();
        if (entities == null) return list;
        for (PostEntity e : entities) {
            list.add(fromPostEntity(e));
        }
        return list;
    }

    public static Post fromPostEntity(PostEntity e) {
        if (e == null) return null;
        Post p = new Post();
        p.id = e.id;
        p.title = e.title;
        p.content = e.content;
        // Map từ Entity sang Post
        p.imageUrl = e.thumbnailUrl;
        
        p.categoryId = e.categoryId;
        p.timestamp = e.publishedAt;
        return p;
    }

    // --- Bổ sung Mapper cho Category ---
    public static List<CategoryEntity> toCategoryEntities(List<Category> categories) {
        List<CategoryEntity> list = new ArrayList<>();
        if (categories == null) return list;
        for (Category c : categories) {
            CategoryEntity e = new CategoryEntity();
            e.id = c.id;
            e.name = c.name;
            list.add(e);
        }
        return list;
    }

    public static List<Category> fromCategoryEntities(List<CategoryEntity> entities) {
        List<Category> list = new ArrayList<>();
        if (entities == null) return list;
        for (CategoryEntity e : entities) {
            Category c = new Category();
            c.id = e.id;
            c.name = e.name;
            list.add(c);
        }
        return list;
    }
}
