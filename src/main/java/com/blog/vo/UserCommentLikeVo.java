package com.blog.vo;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class UserCommentLikeVo implements Serializable {
    private Long commentLikes;
    private Long commentCount;
    private Set<CommentVo> commentVos;

    public UserCommentLikeVo(Long commentLikes, Long commentCount, Set<CommentVo> commentVos) {
        this.commentLikes = commentLikes;
        this.commentCount = commentCount;
        this.commentVos = commentVos;
    }
    public UserCommentLikeVo(Long commentLikes, Long commentCount) {
        this.commentLikes = commentLikes;
        this.commentCount = commentCount;
    }
}
