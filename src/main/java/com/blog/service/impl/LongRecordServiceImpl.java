package com.blog.service.impl;

import com.blog.dao.LoginHistoryPoRepository;
import com.blog.dto.LoginHistoryDto;
import com.blog.mapper.LoginHistoryPoMapper;
import com.blog.po.LoginHistoryPo;
import com.blog.service.LongRecordService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class LongRecordServiceImpl implements LongRecordService {
    private final LoginHistoryPoRepository loginHistoryRepository;

    @Override
    public Page<LoginHistoryDto> getLoginRecords(String username, String ipAddress, String action, Integer page, Integer pageSize) {
        Specification<LoginHistoryPo> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!ObjectUtils.isEmpty(username)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("username"), username));
            }
            if (!ObjectUtils.isEmpty(ipAddress)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("ipAddress"), ipAddress));
            }
            if (!ObjectUtils.isEmpty(action)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("action"), action));
            }
            return predicate;
        };
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<LoginHistoryPo> loginHistoryPos = loginHistoryRepository.findAll(specification, pageable);
        return loginHistoryPos.map(LoginHistoryPoMapper.INSTANCE::toDto);
    }

    @Override
    public LoginHistoryDto getLoginRecord(Long id) {
        return LoginHistoryPoMapper.INSTANCE.toDto(loginHistoryRepository.findById(id).orElse(null));
    }
}
