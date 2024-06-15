package com.blog.service.impl;

import com.blog.dao.PostPoRepository;
import com.blog.dao.RecentViewPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.RecentViewDto;
import com.blog.mapper.RecentViewPoMapper;
import com.blog.po.RecentViewPo;
import com.blog.po.UserPo;
import com.blog.service.RecentViewService;
import com.blog.vo.PostVo;
import com.google.common.base.Strings;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentViewServiceImpl implements RecentViewService {
    //2024-01-01T14:33
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RecentViewPoRepository recentViewPoRepository;
    private final UserPoRepository userJpaRepository;
    private final PostPoRepository postPoRepository;
    @Override
    public Page<PostVo> getRecentView(String username,String authorName, String authorEmail, String title, Integer page, Integer size) {;
        Pageable pageable = PageRequest.of(page - 1, size);
        //利用username 查詢使用者
        if(Strings.isNullOrEmpty(authorName)) {
            throw new UsernameNotFoundException("請輸入作者名稱");
        }
        Optional<UserPo> user = userJpaRepository.findByUserName(authorName);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("找不到該使用者" + authorName);
        }
        Long userId = user.get().getId();
        // 進行where like 查詢
        return recentViewPoRepository.findPostPoByAuthorNameAndAuthorEmailAndTitleAndUserId(authorName, authorEmail, title, userId, pageable);
    }

    @Override
    public void createRecentView(RecentViewDto recentViewDto){
        // 找出對應使用者
        String userName = recentViewDto.getUserName();
        if(Strings.isNullOrEmpty(userName)) {
            throw new UsernameNotFoundException("找不到該用戶");
        }
        RecentViewPo recentViewPo = RecentViewPoMapper.INSTANCE.toPo(recentViewDto);
        userJpaRepository.findByUserName(userName).ifPresent(recentViewPo::setUser);
        // 找出對應文章
        Long postId = recentViewDto.getPostId();
        postPoRepository.findById(postId).ifPresent(
                postPo -> {
                    recentViewPo.setPosts(Collections.singleton(postPo));
                    recentViewPo.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
                }
        );
        recentViewPoRepository.saveAndFlush(recentViewPo);
    }

    @Override
    public PostVo getRecentViewById(Long id) {
        return recentViewPoRepository.findPostVoById(id).orElse(null);
    }

    private String transformDateToString(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }
}
