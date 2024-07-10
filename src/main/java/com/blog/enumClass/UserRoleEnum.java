package com.blog.enumClass;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    SEARCH("ROLE_SEARCH");

    private final String roleName;

    UserRoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public static UserRoleEnum fromString(String roleName) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("沒有這個權限 " + roleName);
    }
}
