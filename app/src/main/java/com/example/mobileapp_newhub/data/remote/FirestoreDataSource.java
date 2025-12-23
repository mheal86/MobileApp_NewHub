package com.example.mobileapp_newhub.data.remote;

import com.example.mobileapp_newhub.model.Category;
import com.example.mobileapp_newhub.model.Comment;
import com.example.mobileapp_newhub.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirestoreDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchPosts(OnSuccessListener<List<Post>> successCallback, OnFailureListener failureCallback) {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Post> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.setId(doc.getId());
                            list.add(post);
                        }
                    }
                    successCallback.onSuccess(list);
                })
                .addOnFailureListener(failureCallback);
    }

    public void fetchCategories(OnSuccessListener<List<Category>> successCallback, OnFailureListener failureCallback) {
        db.collection("categories")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
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

    // NEW: Fetch Comments with Realtime Listener
    public void fetchComments(String postId, OnSuccessListener<List<Comment>> successCallback, OnFailureListener failureCallback) {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                // BỎ orderBy của Firestore để tránh lỗi thiếu Index
                // .orderBy("timestamp", Query.Direction.DESCENDING) 
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        failureCallback.onFailure(e);
                        return;
                    }
                    if (snapshot != null) {
                        List<Comment> list = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment != null) {
                                comment.setId(doc.getId());
                                list.add(comment);
                            }
                        }
                        // Sắp xếp thủ công trong Java (Mới nhất lên đầu)
                        Collections.sort(list, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                        
                        successCallback.onSuccess(list);
                    }
                });
    }

    // NEW: Add Comment
    public void addComment(Comment comment, OnSuccessListener<Void> successCallback, OnFailureListener failureCallback) {
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    successCallback.onSuccess(null);
                })
                .addOnFailureListener(failureCallback);
    }
}
