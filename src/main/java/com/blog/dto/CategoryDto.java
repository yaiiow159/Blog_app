package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.CategoryPo}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto extends BaseDto implements Serializable {

    @NotNull
    @Schema(description = "分類名稱",example = "軟體設計類")
    String name;
    @Schema(description = "分類描述",example = "這是一個描述")
    String description;
}