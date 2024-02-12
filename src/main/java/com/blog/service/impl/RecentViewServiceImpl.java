package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.PostPoRepository;
import com.blog.dao.RecentViewPoRepository;
import com.blog.dao.UserJpaRepository;
import com.blog.dto.PostDto;
import com.blog.dto.RecentViewPoDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.po.PostPo;
import com.blog.po.RecentViewPo;
import com.blog.po.UserPo;
import com.blog.service.RecentViewService;
import com.blog.vo.PostVo;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentViewServiceImpl implements RecentViewService {
    //2024-01-01T14:33
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final RecentViewPoRepository recentViewPoRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostPoRepository postPoRepository;
    @Override
    public Page<PostVo> getRecentView(String dateTime, Long postId, String username, Integer page, Integer size, String sort, String direction) {;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(direction), sort);
        LocalDateTime createTime = dateTime.isEmpty() ? null : LocalDateTime.parse(dateTime, dateTimeFormatter);
        //利用username 查詢使用者
        if(Strings.isNullOrEmpty(username)) {
            throw new UsernameNotFoundException("找不到該使用者");
        }
        Optional<UserPo> user = userJpaRepository.findByUserName(username);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("找不到該使用者");
        }
        Long userId = user.get().getId();
        List<PostVo> postVoList = recentViewPoRepository.findPostPoByUserIdAndCreateTimeBefore(userId, createTime);
        if(!postVoList.isEmpty()) {
            for (PostVo postVo : postVoList) {
                postVo.setCreateTimeStr(transformDateToString(postVo.getCreateTime()));
            }
        }
        return new PageImpl<>(postVoList, pageable, postVoList.size());
    }

    @Override
    public String createRecentView(RecentViewPoDto recentViewPoDto) throws ResourceNotFoundException {
        // 找出對應使用者
        JSONObject jsonObject = new JSONObject();
        String userName = recentViewPoDto.getUserName();
        if(Strings.isNullOrEmpty(userName)) {
            throw new UsernameNotFoundException("找不到該用戶");
        }
        RecentViewPo recentViewPo = new RecentViewPo();
        Optional<UserPo> userPo = userJpaRepository.findByUserName(userName);
        if(userPo.isEmpty()) {
            throw new UsernameNotFoundException("找不到該用戶");
        } else {
            UserPo userPo1 = userPo.get();
            recentViewPo.setUser(userPo1);
        }
        // 找出對應文章
        Long postId = recentViewPoDto.getPostId();
        Optional<PostPo> postPo = postPoRepository.findById(postId);
        if(postPo.isEmpty()) {
            throw new ResourceNotFoundException("找不到該文章");
        } else {
            PostPo postPo1 = postPo.get();
            Set<PostPo> postPoSet = Collections.singleton(postPo1);
            recentViewPo.setPosts(postPoSet);
        }
        recentViewPoRepository.save(recentViewPo);
        jsonObject.put("message", "新增最近瀏覽紀錄成功");
        return jsonObject.toJSONString();
    }

    private String transformDateToString(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }
}
