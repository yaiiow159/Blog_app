package com.blog.service;

import com.blog.dto.RecentViewDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.vo.PostVo;
import org.springframework.data.domain.Page;

public interface RecentViewService {
    Page<PostVo> getRecentView(String dateTime, Long postId, String username, Integer page, Integer size);

    void createRecentView(RecentViewDto recentViewDto) throws ResourceNotFoundException;
}
