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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecentViewServiceImpl implements RecentViewService {

    private final RecentViewPoRepository recentViewPoRepository;
    private final UserPoRepository userJpaRepository;
    private final PostPoRepository postPoRepository;

    @Override
    public Page<PostVo> getRecentView(String username, String authorName, String authorEmail, String title, Integer page, Integer size) throws UsernameNotFoundException {
        Pageable pageable = PageRequest.of(page - 1, size);
        //利用username 查詢使用者
        if (!StringUtils.hasText(authorName)) {
            throw new UsernameNotFoundException("請輸入作者名稱");
        }
        Optional<UserPo> user = userJpaRepository.findByUserName(authorName);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("找不到該使用者" + authorName);
        }
        Long userId = user.get().getId();
        return recentViewPoRepository.findPostPoByAuthorNameAndAuthorEmailAndTitleAndUserId(authorName, authorEmail, title, userId, pageable);
    }

    @Override
    @Transactional
    public void createRecentView(RecentViewDto recentViewDto) throws UsernameNotFoundException {
        // 找出對應使用者
        String userName = recentViewDto.getUserName();
        if (!StringUtils.hasText(userName)) {
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

}
