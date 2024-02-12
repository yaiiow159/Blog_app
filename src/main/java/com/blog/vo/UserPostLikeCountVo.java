package com.blog.vo;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class UserPostLikeCountVo implements Serializable {
    private Long postLikes;
    private Long postCount;
    private Set<PostVo> postVos;

    public UserPostLikeCountVo(Long postLikes, Long postCount, Set<PostVo> postVos) {
        this.postLikes = postLikes;
        this.postCount = postCount;
        this.postVos = postVos;
    }

    public UserPostLikeCountVo(Long postLikes, Long postCount) {
        this.postLikes = postLikes;
        this.postCount = postCount;
    }
}
