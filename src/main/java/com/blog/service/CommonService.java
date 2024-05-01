package com.blog.service;

import com.blog.vo.UserCommentLikeVo;
import com.blog.vo.UserPostLikeCountVo;

public interface CommonService {
    UserCommentLikeVo getUserCommentLikeCount(String username);

    UserPostLikeCountVo getUserPostLikeCount(String username);
}
