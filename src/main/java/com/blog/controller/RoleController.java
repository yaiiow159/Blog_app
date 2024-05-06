package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.RoleDto;
import com.blog.dto.UserDto;
import com.blog.service.RoleService;
import com.blog.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Slf4j
@Tag(name = "角色相關功能", description = "角色相關功能")
public class RoleController {
    @Resource
    private RoleService roleService;

    @Resource
    private UserService userService;

    @GetMapping
    @Operation(summary = "查詢所有角色", description = "查詢所有角色")
    public ApiResponse<Page<RoleDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page") Integer page,
                                               @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize") Integer pageSize,
                                               @Parameter(description = "名稱",example = "role1") @RequestParam(name = "name")String name){
        Page<RoleDto> roleDtoPage = roleService.findAll(name,page,pageSize);
        // 查詢當前權限下的使用者
        if(roleDtoPage.isEmpty()||CollectionUtils.isEmpty(roleDtoPage.getContent()))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", roleDtoPage, HttpStatus.OK);
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有角色", description = "查詢所有角色")
    public ApiResponse<List<RoleDto>> findAll(){
        return new ApiResponse<>(true, "查詢成功", roleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者角色",description = "查詢使用者角色")
    public ApiResponse<RoleDto> getRoleByUserId(@Parameter(description = "使用者id",example = "1")@PathVariable Long id){
        return new ApiResponse<>(true, "查詢成功", roleService.findById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "新增角色",description = "新增角色")
    public ApiResponse<RoleDto> create(@Validated @RequestBody RoleDto roleDto) {
        try {
            roleService.add(roleDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "新增失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "新增成功", HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新角色",description = "更新角色")
    public ApiResponse<RoleDto> update(@Validated @RequestBody RoleDto roleDto){
        try {
            roleService.edit(roleDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }


    @GetMapping("/{id}/users")
    @Operation(summary = "使用角色id查詢角色使用者",description = "查詢角色")
    public ApiResponse<List<UserDto>> findByName(@PathVariable long id){
        List<UserDto> userList = userService.findUsersByRoleName(id);
        if(CollectionUtils.isEmpty(userList))
            return new ApiResponse<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功", userList, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除角色",description = "刪除角色")
    public ApiResponse<String> delete(@PathVariable long id){
        try {
            roleService.delete(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "刪除失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "刪除成功",HttpStatus.OK);
    }
}
