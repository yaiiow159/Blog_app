package com.blog.service.impl;

import com.blog.dao.UserPoRepository;
import com.blog.service.CommonService;
import com.blog.vo.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonServiceImpl implements CommonService {

    private final UserPoRepository userJpaRepository;

    @Override
    @Cacheable(value = "userCommentLikeCount", key = "#username", unless = "#result.commentVos.size() == 0")
    public UserCommentLikeVo getUserCommentLikeCount(String username) {
        UserCommentLikeVo commentLikeCount = userJpaRepository.getCommentLikeCount(username);
        Set<CommentVo> comments = userJpaRepository.getComments(username);
        commentLikeCount.setCommentVos(comments);
        return commentLikeCount;
    }

    @Override
    @Cacheable(value = "userPostLikeCount", key = "#username", unless = "#result.postVos.size() == 0")
    public UserPostLikeCountVo getUserPostLikeCount(String username) {
        UserPostLikeCountVo postLikeCount = userJpaRepository.getPostLikeCount(username);
        Set<PostVo> posts = userJpaRepository.getPosts(username);
        postLikeCount.setPostVos(posts);
        return postLikeCount;
    }

}
