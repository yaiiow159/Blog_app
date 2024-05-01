package com.blog.dto;

import com.blog.po.RecentViewPo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link RecentViewPo}
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class RecentViewPoDto implements Serializable {

    @Schema(description = "id", example = "1")
    Long id;

    @Schema(description = "使用者名稱", example = "John Doe")
    String userName;

    @Schema(description = "文章主鍵", example = "1")
    Long postId;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Schema(description = "創建時間", example = "2022-01-01 00:00:00")
    String createTime;

    @Schema(description = "使用者ID")
    Long userId;
}