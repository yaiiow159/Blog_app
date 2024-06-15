package com.blog.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisLockUtil {

    @Resource(name = "noResubmitThreadPoolExecutor")
    private ThreadPoolExecutor executor;

    @Resource
    private RedissonClient redisson;

    /**
     * 獲取 Redisson 分布式鎖
     * @param lockKey
     * @param uuid
     * @param delaySeconds
     * @param unit
     * @return
     */
    public boolean getLock(String lockKey, final String uuid, long delaySeconds, final TimeUnit unit) {
        RLock rLock = redisson.getLock(lockKey);
        boolean success = false;
        try {
            success = rLock.tryLock(0, delaySeconds, unit);
        } catch (InterruptedException e) {
            log.error("[RedisLock][Rlock]>>>> lock error: ", e);
        }
        return success;
    }

    /**
     * 釋放 Redisson 分布式鎖
     * @param lockKey
     */
    public void Runlock(String lockKey) {
        RLock rLock = redisson.getLock(lockKey);
        log.debug("[RedisLock][Rlock]>>>> {}, status: {} === unlock thread id is: {}", rLock.isHeldByCurrentThread(), rLock.isLocked(),
                Thread.currentThread().getId());
        rLock.unlock();
    }

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
                    e.printStackTrace();
                }
                Runlock(lockKey);
            });
        }
    }


}
