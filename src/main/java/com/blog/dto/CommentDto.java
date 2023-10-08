package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.CommentPo}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CommentDto extends BaseDto implements Serializable {

    @NotNull
    @Schema(description = "評論內容",example = "哈哈哈你好好笑",requiredMode = Schema.RequiredMode.REQUIRED)
    String body;

    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message = "電子郵件格式錯誤")
    @Schema(description = "電子郵件地址",example = "admin1234@example.com")
    String email;

    @NotNull
    @Schema(description = "評論者名稱",example = "alex")
    String name;

    @Valid
    @Schema(description = "所屬文章")
    PostDto post;
}