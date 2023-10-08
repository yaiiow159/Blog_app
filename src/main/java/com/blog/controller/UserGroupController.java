package com.blog.controller;


import com.blog.dto.UserGroupDto;
import com.blog.exception.ValidateFailedException;
import com.blog.service.UserGroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Tag(name = "使用者群組相關功能", description = "使用者群組相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/userGroup")
public class UserGroupController {

    @Resource
    private UserGroupService userGroupService;

    @GetMapping
    @Operation(summary = "查詢所有使用者群組",description = "查詢所有使用者群組")
    public ResponseEntity<Page<UserGroupDto>> findAll(@Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "0" ) int page,
                                                      @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size",defaultValue = "10" ) int size,
                                                      @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id",required = false) String sort,
                                                      @Parameter(description = "排序順序(正序/反序)",example = "ASC/DESC") @RequestParam(name = "direction",defaultValue = "ASC",required = false) String direction) {
        return ResponseEntity.ok(userGroupService.findAll(page, size, sort));
    }

    @GetMapping("/search/{id}")
    @Operation(summary = "使用id查詢使用者群組",description = "查詢使用者群組")
    public ResponseEntity<UserGroupDto> findById(@Parameter(description = "群組id",example = "1")@PathVariable Long id) {
        UserGroupDto userGroupDto = userGroupService.findById(id);
        if(ObjectUtils.isEmpty(userGroupDto))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(userGroupDto, HttpStatus.OK);
    }

    @PostMapping("/searchBySpec")
    @Operation(summary = "查詢使用者群組",description = "動態條件查詢使用者群組")
    public ResponseEntity<Page<UserGroupDto>> findBySpec(
            @Parameter(description = "群組名稱",example = "group1") @RequestParam(name = "groupName") String groupName,
            @Parameter(description = "群組描述",example = "群組描述") @RequestParam(name = "description",required = false) String description,
            @Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "0") int page,
            @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size",defaultValue = "10") int size,
            @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id",required = false) String sort,
            @Parameter(description = "排序順序(正序/反序)",example = "ASC/DESC") @RequestParam(name = "direction",defaultValue = "ASC",required = false) String direction) {

        Page<UserGroupDto> userGroupDtoPage = userGroupService.findBySpec(groupName, description, page, size, sort);
        if(CollectionUtils.isEmpty(userGroupDtoPage.getContent()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(userGroupDtoPage);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "創建使用者群組",description = "創建使用者群組")
    public ResponseEntity<UserGroupDto> create(@Parameter(name = "群組")@RequestBody UserGroupDto userGroupDto) throws ValidateFailedException {
        UserGroupDto userGroupDto1 = userGroupService.createGroup(userGroupDto);
        if(ObjectUtils.isEmpty(userGroupDto1))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(userGroupDto1, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    @Operation(summary = "更新使用者群組",description = "更新使用者群組")
    public ResponseEntity<UserGroupDto> update(@Parameter(name = "群組")@RequestBody UserGroupDto userGroupDto) throws ValidateFailedException {
        return ResponseEntity.ok(userGroupService.updateGroup(userGroupDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "刪除使用者群組",description = "刪除使用者群組")
    public ResponseEntity<String> delete(@Parameter(description = "群組id",example = "1")@PathVariable Long id) throws ValidateFailedException {
        return ResponseEntity.ok(userGroupService.deleteGroup(id));
    }
}
