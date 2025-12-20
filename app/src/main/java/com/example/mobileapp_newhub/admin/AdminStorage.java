package com.example.mobileapp_newhub.admin;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AdminStorage {

    public interface OnUploadOk {
        void onOk(String downloadUrl, String storagePath);
    }
    public interface OnErr {
        void onErr(String err);
    }
    public interface OnDone {
        void onDone();
    }

    public static void uploadPostImage(Uri fileUri, OnUploadOk ok, OnErr err) {
        if (fileUri == null) { err.onErr("No image selected"); return; }

        String path = "post_images/" + UUID.randomUUID() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);

        ref.putFile(fileUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> ok.onOk(uri.toString(), path))
                .addOnFailureListener(e -> err.onErr(e.getMessage()));
    }

    public static void deleteImageByPath(String storagePath, OnDone ok, OnErr err) {
        if (storagePath == null || storagePath.trim().isEmpty()) { ok.onDone(); return; }

        FirebaseStorage.getInstance().getReference()
                .child(storagePath)
                .delete()
                .addOnSuccessListener(v -> ok.onDone())
                .addOnFailureListener(e -> err.onErr(e.getMessage()));
    }
}
