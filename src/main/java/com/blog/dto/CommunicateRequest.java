package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunicateRequest implements Serializable {
    @Schema(description = "姓名", example = "TimmyChung")
    private String fromUser;
    @Schema(description = "郵箱", example = "5HqKz@example.com")
    private String email;
    @Schema(description = "聯繫內容", example = "Hello World")
    private String content;
}
