package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum GroupAuthEnum {
    USER("一般使用者"),
    ADMIN("管理員"),;

    private final String status;

    GroupAuthEnum(String status) {
        this.status = status;
    }

    public static GroupAuthEnum fromString(String status) {
        for (GroupAuthEnum groupStatus : GroupAuthEnum.values()) {
            if (groupStatus.getStatus().equalsIgnoreCase(status)) {
                return groupStatus;
            }
        }
        throw new IllegalArgumentException("沒有這個狀態 " + status);
    }
}
