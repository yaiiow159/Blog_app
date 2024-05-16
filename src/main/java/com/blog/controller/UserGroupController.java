package com.blog.controller;


import com.blog.annotation.NoResubmit;
import com.blog.dto.ApiResponse;
import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import com.blog.service.UserGroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "使用者群組相關功能", description = "使用者群組相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
public class UserGroupController {
    @Resource
    private UserGroupService userGroupService;

    @GetMapping
    @Operation(summary = "查詢所有使用者群組",description = "查詢所有使用者群組")
    public ApiResponse<Page<UserGroupDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1" ) int page,
                                                   @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10" ) int pageSize,
                                                   @Parameter(description = "群組名稱",example = "group1") @RequestParam(name = "groupName",required = false) String groupName,
                                                   @Parameter(description = "覆核等級",example = "1") @RequestParam(name = "reviewLevel",required = false) String reviewLevel) {
        Page<UserGroupDto> userGroupDtoPage = userGroupService.findAll(page,pageSize,groupName,reviewLevel);
        if(userGroupDtoPage.isEmpty()|| CollectionUtils.isEmpty(userGroupDtoPage.getContent()))
            return new ApiResponse<>(false, "查無資料", userGroupDtoPage, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", userGroupDtoPage, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者群組",description = "查詢使用者群組")
    public ApiResponse<UserGroupDto> getRoleByUserId(@Parameter(description = "使用者id",example = "1")@PathVariable Long id){
        return new ApiResponse<>(true, "查詢成功", userGroupService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有使用者群組",description = "查詢所有使用者群組")
    public ApiResponse<List<UserGroupDto>> findAll(){
        return new ApiResponse<>(true, "查詢成功", userGroupService.findAll(), HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "創建使用者群組",description = "創建使用者群組")
    public ApiResponse<UserGroupDto> create(@Parameter(name = "群組")@RequestBody UserGroupDto userGroupDto) throws ValidateFailedException {
        try {
            userGroupService.add(userGroupDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "創建失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        userGroupService.add(userGroupDto);
        return new ApiResponse<>(true, "創建成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新使用者群組",description = "更新使用者群組")
    public ApiResponse<UserGroupDto> update(@Parameter(name = "群組")@RequestBody UserGroupDto userGroupDto) throws ValidateFailedException {
        try {
            userGroupService.edit(userGroupDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除使用者群組",description = "刪除使用者群組")
    public ApiResponse<String> delete(@Parameter(description = "群組id",example = "1")@PathVariable Long id) throws ValidateFailedException {
        try {
            userGroupService.delete(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "刪除失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "刪除成功", HttpStatus.OK);
    }
}
