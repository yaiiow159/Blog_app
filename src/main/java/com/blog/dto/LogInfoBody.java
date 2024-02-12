package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
public class LogInfoBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "userAgent")
    private String userAgent;

    @Schema(description = "basePath")
    private String basePath;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "method")
    private String method;

    @Schema(description = "parameter")
    private String[] parameter;

    @Schema(description = "result")
    private String result;

    @Schema(description = "spendTime")
    private String spendTime;

    @Schema(description = "startTime")
    private String startTime;

    @Schema(description = "url")
    private String url;
}
