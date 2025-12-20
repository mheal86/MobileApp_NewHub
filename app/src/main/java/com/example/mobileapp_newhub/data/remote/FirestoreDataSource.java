package com.example.mobileapp_newhub.data.remote;

import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
// SỬA IMPORT Ở ĐÂY
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FirestoreDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchPosts(com.google.android.gms.tasks.OnSuccessListener<List<Post>> successCallback, OnFailureListener failureCallback) {
        db.collection("posts")
                // SỬA Ở ĐÂY: Dùng đúng class Query
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Post> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.id = doc.getId();
                            list.add(post);
                        }
                    }
                    successCallback.onSuccess(list);
                })
                .addOnFailureListener(failureCallback);
    }

    public void fetchCategories(com.google.android.gms.tasks.OnSuccessListener<List<Category>> successCallback, OnFailureListener failureCallback) {
        db.collection("categories").get()
                .addOnSuccessListener(snapshot -> {
                    List<Category> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Category category = doc.toObject(Category.class);
                        if (category != null) {
                            category.id = doc.getId();
                            list.add(category);
                        }
                    }
                    successCallback.onSuccess(list);
                })
                .addOnFailureListener(failureCallback);
    }
}
