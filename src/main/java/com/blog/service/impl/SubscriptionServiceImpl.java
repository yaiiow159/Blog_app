package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.SubscriptionPoRepository;
import com.blog.dto.PostDto;
import com.blog.dto.UserDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.SubscriptionPoMapper;
import com.blog.dto.SubscriptionDto;
import com.blog.po.SubscriptionPo;
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
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPoRepository subscriptionPoRepository;
    private final UserService userService;
    private final PostService postService;

    /**
     * 訂閱文章
     *
     * @param username 訂閱該篇文章的使用者id
     * @param postId 被訂閱的文章id
     */
    @Override
    public String subscribe(String username, Long postId, String authorName, String email) throws ResourceNotFoundException {
        JSONObject jsonObject = new JSONObject();
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        // 查詢對應的user
        UserDto userDto = userService.findByUserName(username);
        subscriptionDto.setUser(userDto);
        // 查詢對應的post
        PostDto post = postService.getOnePost(postId);
        subscriptionDto.setPost(post);
        subscriptionDto.setAuthorName(authorName);
        subscriptionDto.setEmail(email);
        SubscriptionPo subscriptionPo = SubscriptionPoMapper.INSTANCE.toPo(subscriptionDto);
        subscriptionPoRepository.saveAndFlush(subscriptionPo);
        jsonObject.put("message", "訂閱成功");
        return jsonObject.toJSONString();
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
        JSONObject jsonObject = new JSONObject();
        subscriptionPoRepository.deleteByUsernameAndPostId(username, postId);
        if(subscriptionPoRepository.existsByUsernameAndPostId(username, postId) > 0){
            jsonObject.put("message", "取消訂閱失敗");
            return jsonObject.toJSONString();
        }
        jsonObject.put("message", "取消訂閱成功");
        return jsonObject.toJSONString();
    }

    @Override
    public List<SubscriptionDto> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail) {
        List<SubscriptionPo> subscriptionPoList = subscriptionPoRepository.findByAuthorNameOrEmail(authorName, authorEmail);
        return SubscriptionPoMapper.INSTANCE.toDtoList(subscriptionPoList);
    }

    @Override
    public String checkSubscription(String username, Long postId) {
        JSONObject jsonObject = new JSONObject();
        if(subscriptionPoRepository.existsByUsernameAndPostId(username, postId) > 0){
            jsonObject.put("message", "已訂閱");
            return jsonObject.toJSONString();
        }
        jsonObject.put("message", "未訂閱");
        return jsonObject.toJSONString();
    }
}
