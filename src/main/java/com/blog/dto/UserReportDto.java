package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class UserReportDto extends BaseDto implements Serializable {
    Long id;
    boolean status;
    String reason;
    LocalDateTime reportTime;
}