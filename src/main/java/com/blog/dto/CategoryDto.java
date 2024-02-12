package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.CategoryPo}
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto extends BaseDto implements Serializable {
    @NotBlank(message = "分類名稱不能為空")
    @Schema(description = "分類名稱",example = "軟體設計類",requiredMode = Schema.RequiredMode.REQUIRED)
    String name;
    @Schema(description = "分類描述",example = "這是一個描述")
    String description;
}