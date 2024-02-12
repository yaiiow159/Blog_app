package com.blog.service;

import com.blog.dto.PostHistoryPoDto;
import com.blog.po.PostHistoryPo;
import com.blog.po.PostPo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PostHistoryService {
    PostHistoryPo transformToHistory(PostPo postPo);
    void deleteHistoryWherePostIdIn(List<Long> postIds);

    void saveAll(List<PostHistoryPo> postHistoryPos);

    List<PostHistoryPo> findAll();

    Page<PostHistoryPoDto> getHistoryPosts(String title, String authorName, String startTime, String endTime, int page, int size, String sort, String direction);
}
