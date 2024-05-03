package com.blog.dto;

import com.blog.po.LoginHistoryPo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link LoginHistoryPo}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class    LoginHistoryDto implements Serializable {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "使用者名稱")
    private String username;
    @Schema(description = "登入IP位址")
    private String ipAddress;
    @Schema(description = "執行動作")
    private String action;
    @Schema(description = "瀏覽器資訊")
    private String userAgent;

    @Schema(description = "登入時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTimestamp;
    @Schema(description = "登出時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime logoutTimestamp;
}