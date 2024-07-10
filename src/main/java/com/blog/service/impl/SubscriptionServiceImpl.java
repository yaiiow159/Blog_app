package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.PostPoRepository;
import com.blog.dao.SubscriptionPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.PostDto;
import com.blog.dto.UserDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.SubscriptionPoMapper;
import com.blog.dto.SubscriptionDto;
import com.blog.po.PostPo;
import com.blog.po.SubscriptionPo;
import com.blog.po.UserPo;
import com.blog.service.PostService;
import com.blog.service.SubscriptionService;
import com.blog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPoRepository subscriptionPoRepository;
    private final UserPoRepository userPoRepository;
    private final PostPoRepository postPoRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    /**
     * 訂閱文章
     *
     * @param username 訂閱該篇文章的使用者id
     * @param postId 被訂閱的文章id
     */
    @Override
    @Transactional
    public void subscribe(String username, Long postId, String authorName, String email) {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setAuthorName(authorName);
        subscriptionDto.setEmail(email);
        SubscriptionPo subscriptionPo = SubscriptionPoMapper.INSTANCE.toPo(subscriptionDto);
        UserPo userPo = userPoRepository.findByUserName(username).orElseThrow(() -> new EntityNotFoundException("找不到使用者"));
        subscriptionPo.setUser(userPo);
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("找不到文章"));
        subscriptionPo.setPost(postPo);

        logger.debug("訂閱文章資訊: {}", subscriptionPo);
        // 防止二次訂閱
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("bookmark" + username + "_" + postId))) {
            throw new ValidateFailedException("已訂閱、請勿重複操作");
        }
        stringRedisTemplate.opsForValue().set("bookmark" + username + "_" + postId, "true");
        subscriptionPoRepository.saveAndFlush(subscriptionPo);
    }

    /**
     * 取消訂閱文章
     *
     * @param username 取消訂閱文章的使用者id
     * @param postId 被取消訂閱的文章id
     */
    @Override
    @Transactional
    public void unSubscribe(String username, Long postId){
        // 防止二次取消
        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey("bookmark" + username + "_" + postId))){
            throw new ValidateFailedException("已訂閱、請勿重複操作");
        }
        stringRedisTemplate.delete("bookmark" + username + "_" + postId);

        logger.debug("取消訂閱文章資訊: {}", username + "_" + postId);
        subscriptionPoRepository.deleteByAuthorNameAndPostId(username, postId);
    }

    /**
     * 搜尋對應作者名稱以及作者信箱的訂閱資訊
     *
     * @param authorName 作者名稱
     * @param authorEmail 作者信箱
     * @return List<SubscriptionDto> 訂閱資訊集合
     */
    @Override
    public List<SubscriptionDto> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail) {
        List<SubscriptionPo> subscriptionPoList = subscriptionPoRepository.findByAuthorNameOrEmail(authorName, authorEmail);
        return SubscriptionPoMapper.INSTANCE.toDtoList(subscriptionPoList);
    }

    /**
     * 檢查是否已訂閱
     *
     * @param username 使用者名稱
     * @param postId 文章id
     * @return Boolean 是否已訂閱
     */
    @Override
    public boolean checkSubscription(String username, Long postId) {
        // 雙重檢查 預防 redis 異常 如果沒有檢查到key 再去db檢查
        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey("bookmark" + username + "_" + postId))) {
            SubscriptionPo subscriptionPo = subscriptionPoRepository.findByAuthorNameAndPostId(username, postId).orElse(null);
            return subscriptionPo != null;
        }
        return false;
    }
}
