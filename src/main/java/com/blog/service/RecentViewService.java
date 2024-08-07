package com.blog.service;

import com.blog.dto.RecentViewDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.vo.PostVo;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface RecentViewService {
    Page<PostVo> getRecentView(String username,String authorName, String authorEmail, String title, Integer page, Integer size) throws UsernameNotFoundException;

    void createRecentView(RecentViewDto recentViewDto) throws ResourceNotFoundException;

    PostVo getRecentViewById(Long id);
}
