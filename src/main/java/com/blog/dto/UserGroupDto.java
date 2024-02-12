package com.blog.dto;

import com.blog.enumClass.ReviewLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.UserGroupPo}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDto extends BaseDto implements Serializable {

    @Schema(description = "群組名稱")
    @NotBlank(message = "群組名稱不得為空")
    String groupName;

    @Schema(description = "群組描述")
    String description;

    @Schema(description = "覆核權限等級")
    String reviewLevel;

    @Schema(description = "使用者id")
    Long userId;
}