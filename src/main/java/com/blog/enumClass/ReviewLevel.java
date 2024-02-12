package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum ReviewLevel {
    ADMIN("ADMIN"),
    USER("USER"),
    SEARCH_ONLY("SEARCH_ONLY");

    private final String roleName;
    ReviewLevel(String roleName) {
        this.roleName = roleName;
    }

    public static ReviewLevel fromString(String roleName) {
        for (ReviewLevel role : ReviewLevel.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("沒有這個權限 " + roleName);
    }
}

