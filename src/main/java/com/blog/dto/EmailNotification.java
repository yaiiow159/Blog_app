package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailNotification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "動作")
    private String action;
    @Schema(description = "接收郵件的人")
    private String consumer;
    @Schema(description = "郵件地址")
    private String emailAddress;
    @Schema(description = "郵件主旨")
    private String subObject;
    @Schema(description = "郵件內容")
    private String content;
}
