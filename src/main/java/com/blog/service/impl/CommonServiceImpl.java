package com.blog.service.impl;

import com.blog.dao.UserPoRepository;
import com.blog.service.CommonService;
import com.blog.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonServiceImpl implements CommonService {

    private final UserPoRepository userJpaRepository;

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
