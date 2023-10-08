package com.blog.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.blog.po.PostPo}
 */
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
public class PostDto extends BaseDto implements Serializable {

    @NotNull
    @Schema(description = "文章內文")
    String content;

    @NotNull
    @Schema(description = "文章標題")
    String title;

    @Schema(description = "文章描述")
    String description;

    @Schema(description = "文章分類")
    CategoryDto category;

    @Schema(description = "文章作者名稱")
    private String authorName;

    @Schema(description = "文章作者電子郵件")
    private String authorEmail;

}