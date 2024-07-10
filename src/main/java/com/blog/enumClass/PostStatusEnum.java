package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum PostStatusEnum {

    DRAFT("草稿"),
    PUBLISHED("發佈");

    private final String status;

    PostStatusEnum(String status) {
        this.status = status;
    }

    public static PostStatusEnum fromString(String status) {
        for (PostStatusEnum postStatus : PostStatusEnum.values()) {
            if (postStatus.getStatus().equalsIgnoreCase(status)) {
                return postStatus;
            }
        }
        throw new IllegalArgumentException("沒有這個狀態 " + status);
    }
}
