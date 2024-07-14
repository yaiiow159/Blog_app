package com.blog.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
public class JwtResponseBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "使用者帳戶",requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

    @Schema(description = "jwt類型",defaultValue = "bearer")
    private String type;

    @Schema(description = "jwt令牌",requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @Schema(description = "使用者角色",requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

}
