package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.TagPo}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDto extends BaseDto implements Serializable {
    @Schema(description = "標籤名稱",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "標籤名稱不可為空")
    String name;
    @Schema(description = "標籤描述")
    String description;
}