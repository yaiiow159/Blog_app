package com.blog.service;

import com.blog.dto.UserReportDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService extends BaseService<UserReportDto> {
    List<UserReportDto> findByStatusIsPending();
    void accept(Long id);
    void reject(Long id);
    void batchAccept(List<Long> ids);
    void batchReject(List<Long> ids);
    Page<UserReportDto> findAll(Integer page, Integer pageSize, String reason, String status);

    UserReportDto findByUserId(Long userId);
}
