package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestBody implements Serializable {

    @NotNull
    @Schema(description = "名稱")
    private String name;
    @NotNull
    @Schema(description = "電子郵件")
    private String email;
}
