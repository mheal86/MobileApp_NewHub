package com.example.mobileapp_newhub.data.repository;

// Interface này đóng vai trò là một callback để nhận dữ liệu từ Repository
// khi các tác vụ (như tải từ mạng) hoàn thành.
public interface OnRepositoryCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
