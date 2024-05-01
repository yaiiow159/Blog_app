package com.blog.service;

import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserGroupService {

    UserGroupDto findById(Long userGroupId);

    Page<UserGroupDto> findAll(int page, int size, String groupName, String reviewLevel);

    List<UserGroupDto> findAll();
    void add(UserGroupDto userGroupDto) throws ValidateFailedException;

    void edit(UserGroupDto userGroupDto) throws ValidateFailedException;

    String delete(Long userGroupId) throws ValidateFailedException;
}
