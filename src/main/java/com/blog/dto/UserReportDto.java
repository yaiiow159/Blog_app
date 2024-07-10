package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.blog.po.UserReportPo}
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserReportDto implements Serializable {
    @Schema(description = "使用者名稱")
    String username;

    @Schema(description = "使用者ID")
    Long id;

    @Schema(description = "檢舉狀態")
    boolean status;

    @Schema(description = "檢舉原因")
    String reason;

    @Schema(description = "檢舉時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
    LocalDateTime reportTime;
}