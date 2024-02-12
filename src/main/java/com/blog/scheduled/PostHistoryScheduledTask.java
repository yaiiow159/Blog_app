package com.blog.scheduled;


import com.blog.po.PostHistoryPo;
import com.blog.po.PostPo;
import com.blog.service.PostHistoryService;
import com.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
public class PostHistoryScheduledTask {
    private final PostService postService;
    private final PostHistoryService postHistoryService;
    // 每一小時執行一次
    @Scheduled(cron = "0 0 0 * * *")
    public void executePostHistoryTask() {
        LocalDateTime expireDate = LocalDateTime.now().minusHours(1);
        // 取得目前歷史文章所有數據
        try {
            log.info("executePostHistoryTask start");
            List<PostHistoryPo> postHistoryPos = postHistoryService.findAll();
            // 如果歷史文章的postId沒有在文章的id當中，則刪除該筆歷史文章
            if(!postHistoryPos.isEmpty()) {
                postHistoryPos = postHistoryPos.stream()
                        .filter(postHistoryPo -> !postService.existsByPostId(postHistoryPo.getPostId())).collect(Collectors.toList());
                postHistoryService.deleteHistoryWherePostIdIn(postHistoryPos.stream().map(PostHistoryPo::getPostId).collect(Collectors.toList()));
            }
            // 取得歷史文章數據
            List<PostPo> oldPosts = postService.findByCreateDateBefore(expireDate);
            List<PostHistoryPo> postHistoryPoList = new LinkedList<>();
            for (PostPo postPo : oldPosts) {
                PostHistoryPo postHistoryPo = postHistoryService.transformToHistory(postPo);
                postHistoryPoList.add(postHistoryPo);
            }
            // 寫入歷史文章數據
            postHistoryService.saveAll(postHistoryPoList);
            log.info("executePostHistoryTask end");
        } catch (Exception e) {
            log.error("executePostHistoryTask error", e);
        }
    }
}
