package com.blog.dto;

import com.blog.po.RolePo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;
;

/**
 * DTO for {@link RolePo}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto extends BaseDto implements Serializable {
    @Schema(description = "角色名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名稱不能為空")
    String roleName;

    @Schema(description = "角色描述")
    String description;
}