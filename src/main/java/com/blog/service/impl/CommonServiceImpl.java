package com.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.blog.dao.UserJpaRepository;
import com.blog.service.CommonService;
import com.blog.vo.*;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserCommentLikeVo getUserCommentLikeCount(String username) {
        UserCommentLikeVo commentLikeCount = userJpaRepository.getCommentLikeCount(username);
        Set<CommentVo> comments = userJpaRepository.getComments(username);
        commentLikeCount.setCommentVos(comments);
        return commentLikeCount;
    }

    @Override
    public UserPostLikeCountVo getUserPostLikeCount(String username) {
        UserPostLikeCountVo postLikeCount = userJpaRepository.getPostLikeCount(username);
        Set<PostVo> posts = userJpaRepository.getPosts(username);
        postLikeCount.setPostVos(posts);
        return postLikeCount;
    }

}
