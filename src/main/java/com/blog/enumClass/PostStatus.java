package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum PostStatus {
    PUBLISHED("published"),
    DRAFT("draft");

    private final String status;
    PostStatus(String status) {
        this.status = status;
    }

    public static PostStatus getByStatus(String status) {
        for (PostStatus postStatus : PostStatus.values()) {
            if (postStatus.getStatus().equals(status)) {
                return postStatus;
            }
        }
        return null;
    }
}
