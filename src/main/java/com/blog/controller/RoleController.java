package com.blog.controller;

import com.blog.dto.RoleDto;
import com.blog.dto.UserDto;
import com.blog.service.RoleService;
import com.blog.service.UserService;

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
                                                 @Parameter(description = "排序",example = "id/roleName") @RequestParam(name = "sort")String sort,
                                                 @Parameter(description = "排序方向",example = "DESC/ACS")@RequestParam(name = "direction")String direction){
        Page<RoleDto> roleDtoPage = roleService.findAll(page, size, sort,direction);
        if(CollectionUtils.isEmpty(roleDtoPage.getContent()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(roleDtoPage, HttpStatus.OK);
    }

    @GetMapping("/search/{id}")
    @Operation(summary = "使用id查詢角色",description = "查詢角色")
    public ResponseEntity<RoleDto> findById(@PathVariable Long id){
        RoleDto roleDto = roleService.findByRoleId(id);
        if(ObjectUtils.isEmpty(roleDto))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(roleDto, HttpStatus.OK);
    }

    @GetMapping("/searchUser/{id}")
    @Operation(summary = "查詢具有該角色的使用者")
    public ResponseEntity<List<UserDto>> searchUser(@Parameter(description = "權限id",example = "1")@PathVariable Long id){
        List<UserDto> userDtoList = userService.findUsersByRoleId(id);
        if(CollectionUtils.isEmpty(userDtoList))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "創建角色",description = "創建角色")
    public ResponseEntity<RoleDto> create(@RequestBody RoleDto roleDto){
        RoleDto roleDto1 = roleService.createRole(roleDto);
        if(ObjectUtils.isEmpty(roleDto1))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(roleDto1, HttpStatus.CREATED);
    }
}
