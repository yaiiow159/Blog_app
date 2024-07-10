package com.blog.service;

import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserGroupService extends BaseService<UserGroupDto> {
    Page<UserGroupDto> findAll(int page, int pageSize, String groupName, String reviewLevel) throws Exception;
}
