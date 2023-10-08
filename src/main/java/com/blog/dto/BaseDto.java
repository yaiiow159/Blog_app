package com.blog.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Setter
@Getter
public class BaseDto implements Serializable {

    @Schema(description = "創建物件id",example = "1")
    Long id;

    @Schema(description = "創建物件名稱",example = "true/false")
    Boolean isDeleted;

    @Schema(description = "創建物件者",example = "admin")
    String creatUser;

    @Schema(description = "創建物件日期",example = "2022-01-01 00:00:00")
    LocalDateTime createDate;

    @Schema(description = "更新物件者",example = "admin")
    String updateUser;

    @Schema(description = "更新物件日期",example = "2022-01-01 00:00:00")
    LocalDateTime updDate;
}
