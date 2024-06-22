package com.blog.service;

import com.blog.dto.UserReportDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    Page<UserReportDto> findAll(Integer page, Integer pageSize, String reason, Integer status);

    List<UserReportDto> findAll();

    UserReportDto findById(Long id);

    String accept(Long id);

    String reject(Long id);

    String batchAccept(List<Long> ids);

    String batchReject(List<Long> ids);

    List<UserReportDto> findByStatusIsPending();
}
