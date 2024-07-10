package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum CommentReportEnum {
    ACCEPT("通過"),
    REJECT("拒絕"),
    PENDING("處理中");
    private final String status;

    CommentReportEnum(String status) {
        this.status = status;
    }
}
