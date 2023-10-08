package com.blog.service;

import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import org.springframework.data.domain.Page;

public interface UserGroupService {

    UserGroupDto findById(Long userGroupId);
    UserGroupDto findByGroupName(String groupName);

    Page<UserGroupDto> findAll(int page, int size, String sort);

    Page<UserGroupDto> findBySpec(String groupName, String description, int page, int size, String sort);

    UserGroupDto createGroup(UserGroupDto userGroupDto) throws ValidateFailedException;

    UserGroupDto updateGroup(UserGroupDto userGroupDto) throws ValidateFailedException;

    String deleteGroup(Long userGroupId) throws ValidateFailedException;
}
