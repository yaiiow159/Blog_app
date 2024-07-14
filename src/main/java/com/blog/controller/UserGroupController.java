package com.blog.controller;


import com.blog.annotation.NoResubmit;
import com.blog.dto.UserGroupDto;
import com.blog.response.ResponseBody;
import com.blog.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "使用者群組相關功能", description = "使用者群組相關功能")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class UserGroupController {
    private final UserGroupService userGroupService;

    @GetMapping
    @Operation(summary = "查詢所有使用者群組",description = "查詢所有使用者群組")
    public ResponseBody<Page<UserGroupDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1" ) int page,
                                                    @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10" ) int pageSize,
                                                    @Parameter(description = "群組名稱",example = "group1") @RequestParam(name = "groupName",required = false) String groupName,
                                                    @Parameter(description = "覆核等級",example = "1") @RequestParam(name = "reviewLevel",required = false) String reviewLevel) {
        Page<UserGroupDto> userGroupDtoPage;
        try {
            userGroupDtoPage = userGroupService.findAll(page,pageSize,groupName,reviewLevel);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", userGroupDtoPage, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者群組",description = "查詢使用者群組")
    public ResponseBody<UserGroupDto> getRoleByUserId(@Parameter(description = "使用者id",example = "1")@PathVariable Long id){
        try {
            return new ResponseBody<>(true, "查詢成功", userGroupService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有使用者群組",description = "查詢所有使用者群組")
    public ResponseBody<List<UserGroupDto>> findAll(){
        List<UserGroupDto> userGroupDtoList;
        try {
            userGroupDtoList = userGroupService.findAll();
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", userGroupDtoList, HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "創建使用者群組",description = "創建使用者群組")
    public ResponseBody<UserGroupDto> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
                                             @RequestBody @Validated UserGroupDto userGroupDto) {
        try {
            userGroupService.save(userGroupDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "創建失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "創建成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新使用者群組",description = "利用此API更新使用者群組使用")
    public ResponseBody<UserGroupDto> update(@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
                                             @RequestBody UserGroupDto userGroupDto) {
        try {
            userGroupService.update(userGroupDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除使用者群組",description = "刪除使用者群組")
    public ResponseBody<String> delete(@Parameter(description = "群組id",example = "1")@PathVariable Long id) {
        try {
            userGroupService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗 原因: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功", HttpStatus.OK);
    }
}
