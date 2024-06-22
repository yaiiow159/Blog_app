package com.blog.service.impl;

import com.blog.dao.UserReportPoRepository;
import com.blog.dto.UserReportDto;
import com.blog.enumClass.CommentReport;
import com.blog.mapper.UserReportPoMapper;
import com.blog.po.UserReportPo;
import com.blog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final UserReportPoRepository userReportPoRepository;

    @Override
    public Page<UserReportDto> findAll(Integer page, Integer pageSize, String reason, Integer status) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Specification<UserReportPo> specification = (root, query, criteriaBuilder) -> {
            if (reason != null) {
                query.where(criteriaBuilder.like(root.get("reason"), "%" + reason + "%"));
            }
            if (status != null) {
                query.where(criteriaBuilder.equal(root.get("status"), status));
            }
            return query.getRestriction();
        };
        Page<UserReportPo> userReportPoPage = userReportPoRepository.findAll(specification, pageRequest);
        return UserReportPoMapper.INSTANCE.toDtoPage(userReportPoPage);
    }

    @Override
    public List<UserReportDto> findAll() {
        List<UserReportPo> userReportPos = userReportPoRepository.findAll();
        return UserReportPoMapper.INSTANCE.toDtoList(userReportPos);
    }

    @Override
    public List<UserReportDto> findByStatusIsPending() {
        List<UserReportPo> userReportPos = userReportPoRepository.findByStatus(CommentReport.PENDING.getStatus());
        return UserReportPoMapper.INSTANCE.toDtoList(userReportPos);
    }
    @Override
    public UserReportDto findById(Long id) {
        UserReportDto userReportDto = new UserReportDto();
        Optional<UserReportPo> userReportPo = userReportPoRepository.findById(id);
        if (userReportPo.isPresent()) {
            userReportDto = UserReportPoMapper.INSTANCE.toDto(userReportPo.get());
        }
        return userReportDto;
    }

    @Override
    public String accept(Long id) {
        Optional<UserReportPo> userReportPo = userReportPoRepository.findById(id);
        if (userReportPo.isPresent()) {
            userReportPo.get().setStatus(CommentReport.ACCEPT.getStatus());
            userReportPoRepository.save(userReportPo.get());
            return "放行成功";
        }
        return "放行失敗";
    }

    @Override
    public String reject(Long id) {
        Optional<UserReportPo> userReportPo = userReportPoRepository.findById(id);
        if (userReportPo.isPresent()) {
            userReportPo.get().setStatus(CommentReport.REJECT.getStatus());
            userReportPoRepository.save(userReportPo.get());
            return "拒絕放行成功";
        }
        return "拒絕放行失敗";
    }

    @Override
    public String batchAccept(List<Long> ids) {
        userReportPoRepository.findAllById(ids).forEach(userReportPo -> userReportPo.setStatus(CommentReport.ACCEPT.getStatus()));
        userReportPoRepository.saveAll(userReportPoRepository.findAllById(ids));
        return "批次放行成功";
    }

    @Override
    public String batchReject(List<Long> ids) {
        userReportPoRepository.findAllById(ids).forEach(userReportPo -> userReportPo.setStatus(CommentReport.REJECT.getStatus()));
        userReportPoRepository.saveAll(userReportPoRepository.findAllById(ids));
        return "批次拒絕放行成功";
    }
}
