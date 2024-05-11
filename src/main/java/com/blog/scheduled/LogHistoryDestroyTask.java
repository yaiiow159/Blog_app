package com.blog.scheduled;


import com.blog.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class LogHistoryDestroyTask {

    private final LoginHistoryService loginHistoryService;

    // 每一小時執行一次
    @Scheduled(cron = "0 0 */1 * * *")
    public void executeLogHistoryDestroyTask() {
        // 清除一小時前的紀錄
        log.info("executeLogHistoryDestroyTask start");
        loginHistoryService.deleteLogBefore(LocalDateTime.now().minusDays(1));
        log.info("executeLogHistoryDestroyTask end");
    }
}
