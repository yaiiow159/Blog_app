package com.blog.scheduled;


import com.blog.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class LogHistoryDestroyTask {
    private final LoginHistoryService loginHistoryService;
    // 每一天清理一次
    @Scheduled(cron = "0 0 0 * * ", zone = "Asia/Taipei")
    public void executeLogHistoryDestroyTask() {
        // 清除一天前的紀錄
        try {
            log.info("清除一天前的登入紀錄，清理");
            loginHistoryService.deleteLogBefore();
            log.info("清除一天前的登入紀錄，清理完成");
        } catch (Exception e) {
            log.error("清除一天前的登入紀錄，發生異常 原因: {}", e.getMessage());
        }
    }
}
