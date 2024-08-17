package com.blog.service.impl;

import com.blog.dao.UserReportPoRepository;
import com.blog.dto.UserReportDto;
import com.blog.enumClass.CommentReportEnum;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserReportPoMapper;
import com.blog.po.UserReportPo;
import com.blog.service.ReviewService;
import com.google.common.collect.Lists;
import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserReportPoRepository userReportPoRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private static final int BATCH_SIZE = 10000;

    private static final int MAX_BATCH_SIZE = 1000000;

    @Qualifier("defaultThreadPoolExecutor")
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    /**
     * 新增覆核資訊
     *
     * @param userReportDto 覆核資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void save(UserReportDto userReportDto) throws Exception {
        if (userReportDto == null) {
            throw new IllegalArgumentException("參數為空");
        }
        UserReportPo userReportPo = UserReportPoMapper.INSTANCE.toPo(userReportDto);
        userReportPoRepository.saveAndFlush(userReportPo);
    }

    /**
     * 更新覆核資訊
     *
     * @param userReportDto 覆核資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void update(UserReportDto userReportDto) throws Exception {
        throw new MethodNotSupportedException("暫不支援更新");
    }

    /**
     * 刪除覆核資訊
     *
     * @param userReportDto 覆核資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(UserReportDto userReportDto) throws Exception {
        throw new MethodNotSupportedException("暫不支援刪除");
    }

    /**
     * 刪除覆核資訊
     *
     * @param id 覆核資訊序號
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(Long id) throws Exception {
        throw new MethodNotSupportedException("暫不支援刪除");
    }

    /**
     * 查找特定一筆審核資訊
     *
     * @param id 審核序號
     * @return  審核資訊
     * @throws EntityNotFoundException 查不到該筆資料爆出該異常
     */
    @Override
    public UserReportDto findById(Long id) throws EntityNotFoundException {
        if(id == null){
            throw new IllegalArgumentException("參數 id 不能為 null");
        }
        UserReportPo userReportPo = userReportPoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("查無此資訊"));
        return UserReportPoMapper.INSTANCE.toDto(userReportPo);
    }

    /**
     * 查找所有審核資訊
     *
     * @return 審核資訊集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<UserReportDto> findAll() throws Exception {
        return userReportPoRepository.findAll().stream().map(UserReportPoMapper.INSTANCE::toDto).toList();
    }

    /**
     * 查找分頁審核資訊
     *
     * @param page 當前頁數
     * @param pageSize 每頁顯示筆數
     * @return 審核分頁資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public Page<UserReportDto> findAll(Integer page, Integer pageSize) throws Exception {
        return userReportPoRepository.findAll(PageRequest.of(page, pageSize)).map(UserReportPoMapper.INSTANCE::toDto);
    }

    /**
     * 查找目前待處理中的沈和資訊
     *
     * @return List<UserReportDto> 審核資訊集合
     */
    @Override
    public List<UserReportDto> findByStatusIsPending() {
        List<UserReportPo> userReportPos = userReportPoRepository.findByStatusIsPending(CommentReportEnum.PENDING.getStatus());
        return userReportPos.stream().map(UserReportPoMapper.INSTANCE::toDto).toList();
    }

    /**
     * 審核通過某筆審核資訊
     *
     * @param id 審核序號
     */
    @Override
    public void accept(Long id) {
        userReportPoRepository.updateStatusToAccept(CommentReportEnum.ACCEPT.getStatus(), id);
        logger.debug("審核通過某筆審核資訊序號為:" + id);
    }

    /**
     * 拒絕某筆審核資訊
     *
     * @param id 審核序號
     */
    @Override
    public void reject(Long id) {
        userReportPoRepository.updateStatusToReject(CommentReportEnum.REJECT.getStatus(), id);
        logger.debug("拒絕某筆審核資訊序號為:" + id);
    }

    /**
     * 批量通過審核
     *
     * @param ids 審核資訊序號集合
     */
    @Override
    @Transactional
    public void batchAccept(List<Long> ids) {
        List<List> lists;
        if(ids == null){
            throw new IllegalArgumentException("參數 ids 不能為 null");
        }
        // 驗證 ids大小 是否超過 BATCH_SIZE 如果超過執行 異步執行批量更新
        if(ids.size() >= MAX_BATCH_SIZE){
            // 把 集合拆分成十個子集合一組 100000 / 10 = 10000
            lists = Collections.singletonList(Lists.partition(ids, BATCH_SIZE));
            CountDownLatch countDownLatch = new CountDownLatch(lists.size());
            for (List partitionList : lists) {
                // 異步處理
                CompletableFuture.runAsync(() -> {
                    userReportPoRepository.updateStatusToAcceptInBatch(CommentReportEnum.ACCEPT.getStatus(), partitionList);
                    logger.debug("批量通過審核資訊序號為:" + partitionList);
                    countDownLatch.countDown();
                }, threadPoolTaskExecutor);
            }
            // 等待所有子執行緒完成
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.error("批量通過審核異常 原因: {}", e.getMessage());
                throw new ValidateFailedException("批量通過審核異常 原因: " + e.getMessage());
            }
        }
    }

    /**
     * 批量拒絕審核
     *
     * @param ids 審核資訊序號集合
     */
    @Override
    public void batchReject(List<Long> ids) {
        List<List> lists;
        if(ids == null){
            throw new IllegalArgumentException("參數 ids 不能為 null");
        }
        // 驗證 ids大小 是否超過 BATCH_SIZE 如果超過執行 異步執行批量更新
        if(ids.size() >= MAX_BATCH_SIZE){
            // 把 集合拆分成十個子集合一組 100000 / 10 = 10000
            lists = Collections.singletonList(Lists.partition(ids, BATCH_SIZE));
            CountDownLatch countDownLatch = new CountDownLatch(lists.size());
            for (List partitionList : lists) {
                // 異步處理
                CompletableFuture.runAsync(() -> {
                    userReportPoRepository.updateStatusToRejectInBatch(CommentReportEnum.REJECT.getStatus(), partitionList);
                    logger.debug("批量通過審核資訊序號為:" + partitionList);
                    countDownLatch.countDown();
                }, threadPoolTaskExecutor);
            }
            // 等待所有子執行緒完成
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.error("批量通過審核異常 原因: {}", e.getMessage());
                throw new ValidateFailedException("批量通過審核異常 原因: " + e.getMessage());
            }
        }
    }

    /**
     * @param page
     * @param pageSize
     * @param reason
     * @param status
     * @return
     */
    @Override
    public Page<UserReportDto> findAll(Integer page, Integer pageSize, String reason, String status) {
        return null;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public UserReportDto findByUserId(Long userId) {
        return null;
    }
}
