package com.blog.vo;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankVo implements Serializable {
    private String username;
    private Integer rank;
    private Integer score;
}
