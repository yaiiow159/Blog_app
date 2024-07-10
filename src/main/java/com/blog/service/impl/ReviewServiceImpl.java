package com.blog.service.impl;

import com.blog.dao.UserReportPoRepository;
import com.blog.dto.UserReportDto;
import com.blog.enumClass.CommentReportEnum;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.UserReportPoMapper;
import com.blog.po.UserReportPo;
import com.blog.service.ReviewService;
import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final UserReportPoRepository userReportPoRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

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
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    @Override
    public UserReportDto findById(Long id) throws EntityNotFoundException {
        return null;
    }

    /**
     * @return
     * @throws Exception
     */
    @Override
    public List<UserReportDto> findAll() throws Exception {
        return null;
    }

    /**
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public Page<UserReportDto> findAll(Integer page, Integer pageSize) throws Exception {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<UserReportDto> findByStatusIsPending() {
        return null;
    }

    /**
     * @param id
     */
    @Override
    public void accept(Long id) {

    }

    /**
     * @param id
     */
    @Override
    public void reject(Long id) {

    }

    /**
     * @param ids
     */
    @Override
    public void batchAccept(List<Long> ids) {

    }

    /**
     * @param ids
     */
    @Override
    public void batchReject(List<Long> ids) {

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
