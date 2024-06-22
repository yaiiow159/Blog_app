package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum CommentReport {
    ACCEPT("accept"),
    REJECT("reject"),
    PENDING("pending");
    private final String status;

    CommentReport(String status) {
        this.status = status;
    }
}
