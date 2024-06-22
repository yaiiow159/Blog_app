package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum PostStatus {

    DRAFT("草稿"),
    PUBLISHED("發佈"),
    RECYCLE("回收站");

    private final String status;

    PostStatus(String status) {
        this.status = status;
    }

    public static PostStatus fromString(String status) {
        for (PostStatus postStatus : PostStatus.values()) {
            if (postStatus.getStatus().equalsIgnoreCase(status)) {
                return postStatus;
            }
        }
        throw new IllegalArgumentException("沒有這個狀態 " + status);
    }
}
