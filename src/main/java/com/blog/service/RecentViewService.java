package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.dto.RecentViewPoDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.vo.PostVo;
import org.springframework.data.domain.Page;

public interface RecentViewService {
    Page<PostVo> getRecentView(String dateTime, Long postId, String username, Integer page, Integer size);

    String createRecentView(RecentViewPoDto recentViewPoDto) throws ResourceNotFoundException;
}
