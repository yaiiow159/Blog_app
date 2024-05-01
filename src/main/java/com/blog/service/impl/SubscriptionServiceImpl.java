package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.PostPoRepository;
import com.blog.dao.SubscriptionPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.PostDto;
import com.blog.dto.UserDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.SubscriptionPoMapper;
import com.blog.dto.SubscriptionDto;
import com.blog.po.PostPo;
import com.blog.po.SubscriptionPo;
import com.blog.po.UserPo;
import com.blog.service.PostService;
import com.blog.service.SubscriptionService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPoRepository subscriptionPoRepository;
    private final UserPoRepository userPoRepository;
    private final PostPoRepository postPoRepository;

    /**
     * 訂閱文章
     *
     * @param username 訂閱該篇文章的使用者id
     * @param postId 被訂閱的文章id
     */
    @Override
    public String subscribe(String username, Long postId, String authorName, String email) throws ResourceNotFoundException {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setAuthorName(authorName);
        subscriptionDto.setEmail(email);
        SubscriptionPo subscriptionPo = SubscriptionPoMapper.INSTANCE.toPo(subscriptionDto);
        UserPo userPo = userPoRepository.findByUserName(username).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        subscriptionPo.setUser(userPo);
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("找不到文章"));
        subscriptionPo.setPost(postPo);
        subscriptionPoRepository.saveAndFlush(subscriptionPo);
        return "訂閱成功";
    }

    /**
     * 取消訂閱文章
     *
     * @param username 取消訂閱文章的使用者id
     * @param postId 被取消訂閱的文章id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String unSubscribe(String username, Long postId) {
        subscriptionPoRepository.deleteByUsernameAndPostId(username, postId);
        if(subscriptionPoRepository.existsByUsernameAndPostId(username, postId) > 0){
            return "取消訂閱失敗";
        }
        return "取消訂閱成功";
    }

    @Override
    public List<SubscriptionDto> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail) {
        List<SubscriptionPo> subscriptionPoList = subscriptionPoRepository.findByAuthorNameOrEmail(authorName, authorEmail);
        return SubscriptionPoMapper.INSTANCE.toDtoList(subscriptionPoList);
    }

    @Override
    public String checkSubscription(String username, Long postId) {
        if(subscriptionPoRepository.existsByUsernameAndPostId(username, postId) > 0){
           return "已訂閱";
        }
        return "未訂閱";
    }
}
