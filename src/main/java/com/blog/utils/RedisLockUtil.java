package com.blog.utils;

import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockUtil.class);

    @Resource(name = "noResubmitThreadPoolExecutor")
    private ThreadPoolTaskExecutor executor;

    @Resource
    private RedissonClient redisson;

    /**
     * 獲取 Redisson 分布式鎖
     *
     * @param lockKey  鎖鍵
     * @param uuid     用戶編號
     * @param delaySeconds 鎖定時間
     * @param unit     鎖定時間單位
     * @return 是否獲取鎖
     */
    public boolean getLock(String lockKey, final String uuid, long delaySeconds, final TimeUnit unit) {
        RLock rLock = redisson.getLock(lockKey);
        boolean success = false;
        try {
            success = rLock.tryLock(0, delaySeconds, unit);
        } catch (InterruptedException e) {
            logger.error("Redisson 取得分布式鎖錯誤: {}", e.getMessage());
        }
        return success;
    }

    /**
     * 釋放 Redisson 分布式鎖
     *
     * @param lockKey 鎖鍵
     */
    public void Runlock(String lockKey) {
        RLock rLock = redisson.getLock(lockKey);
        logger.info("Redisson 釋放鎖鍵: {}", lockKey);
        rLock.unlock();
    }

    /**
     *  延遲釋放 Redisson 分布式鎖
     *
     * @param lockKey 鎖鍵
     * @param delayTime 延遲時間
     * @param unit 延遲時間單位
     */
    public void delayUnlock(final String lockKey, long delayTime, TimeUnit unit) {
        if (!StringUtils.hasText(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            Runlock(lockKey);
        } else {
            executor.execute(() -> {
                try {
                    Thread.sleep(unit.toMillis(delayTime));
                } catch (InterruptedException e) {
                    logger.error("Redisson 分布式鎖錯誤: {}", e.getMessage());
                }
                Runlock(lockKey);
            });
        }
    }

    // 設置 watch-dog
    public void setWatchDog(String lockKey) {
        RLock rLock = redisson.getLock(lockKey);
        rLock.forceUnlock();
    }


}
