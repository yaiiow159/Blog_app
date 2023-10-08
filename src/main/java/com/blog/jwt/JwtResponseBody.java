package com.blog.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class JwtResponseBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "使用者帳戶")
    private String account;
    @Schema(description = "jwt類型")
    private String type;
    @Schema(description = "jwt令牌")
    private String token;
}
