package com.blog.controller;

import com.alibaba.fastjson2.JSONObject;
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
    public ResponseEntity<Page<RoleDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page") int page,
                                                 @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "size") int size,
                                                 @Parameter(description = "名稱",example = "role1") @RequestParam(name = "name")String name,
                                                 @Parameter(description = "排序",example = "id/name") @RequestParam(name = "sort")String sort,
                                                 @Parameter(description = "排序方向",example = "DESC/ACS")@RequestParam(name = "direction")String direction){
        Page<RoleDto> roleDtoPage = roleService.findAll(name,page, size, sort,direction);
        // 查詢當前權限下的使用者

        if(CollectionUtils.isEmpty(roleDtoPage.getContent()))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(roleDtoPage);
    }

    @PostMapping("/create")
    @Operation(summary = "新增角色",description = "新增角色")
    public ResponseEntity<RoleDto> create(@Validated @RequestBody RoleDto roleDto) {
        RoleDto roleDto1 = roleService.createRole(roleDto);
        if(ObjectUtils.isEmpty(roleDto1))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(roleDto1);
    }

    @PostMapping("/update")
    @Operation(summary = "更新角色",description = "更新角色")
    public ResponseEntity<RoleDto> update(@Validated @RequestBody RoleDto roleDto){
        RoleDto roleDto1 = roleService.updateRole(roleDto);
        if(ObjectUtils.isEmpty(roleDto1))
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(roleDto1);
    }


    @GetMapping("/{id}/users")
    @Operation(summary = "使用角色id查詢角色使用者",description = "查詢角色")
    public ResponseEntity<Object> findByName(@PathVariable long id){
        JSONObject jsonObject = new JSONObject();
        // 使用角色名稱 來查詢當前角色下的使用者
        List<UserDto> userList = userService.findUsersByRoleName(id);
        if(CollectionUtils.isEmpty(userList)){
            jsonObject.put("message", "沒有使用者");
        } else {
            jsonObject.put("message", "有使用者");
            jsonObject.put("users", userList);
        }
        return ResponseEntity.ok(jsonObject);
    }
}
