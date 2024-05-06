package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum GroupStatus {
    NORMAL("一般使用者"),
    ADMIN("管理員"),;

    private final String status;

    GroupStatus(String status) {
        this.status = status;
    }

    public static GroupStatus fromString(String status) {
        for (GroupStatus groupStatus : GroupStatus.values()) {
            if (groupStatus.getStatus().equalsIgnoreCase(status)) {
                return groupStatus;
            }
        }
        throw new IllegalArgumentException("沒有這個狀態 " + status);
    }
}
