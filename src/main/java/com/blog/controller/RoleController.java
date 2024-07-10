package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.RoleDto;
import com.blog.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Tag(name = "角色相關功能", description = "角色相關功能")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "查詢所有角色", description = "查詢所有角色")
    public ResponseBody<Page<RoleDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page") Integer page,
                                               @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize") Integer pageSize,
                                               @Parameter(description = "名稱",example = "role1") @RequestParam(name = "name", required = false) String name){
        Page<RoleDto> roleDtoPage;
        try {
            roleDtoPage = roleService.findAll(page, pageSize, name);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗 失敗原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "查詢成功", roleDtoPage, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    @Operation(summary = "查詢所有角色", description = "查詢所有角色")
    public ResponseBody<List<RoleDto>> findAll(){
        List<RoleDto> roleDtoList;
        try {
            roleDtoList = roleService.findAll();
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗 失敗原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "查詢成功", roleDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者角色",description = "查詢使用者角色")
    public ResponseBody<RoleDto> getRoleByUserId(@Parameter(description = "使用者id",example = "1")@PathVariable Long id){
        RoleDto roleDto;
        try {
            roleDto = roleService.findById(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "查詢成功", roleDto, HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "新增角色",description = "新增角色")
    public ResponseBody<RoleDto> create(@RequestBody @Validated RoleDto roleDto) {
        try {
            roleService.save(roleDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "新增失敗, 失敗原因為: " + e.getMessage() , null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "新增成功", HttpStatus.CREATED);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新角色",description = "更新角色")
    public ResponseBody<RoleDto> update(@RequestBody @Validated  RoleDto roleDto){
        try {
            roleService.update(roleDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 失敗原因為: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除角色",description = "刪除角色")
    public ResponseBody<String> delete(@PathVariable Long id){
        try {
            roleService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗, 失敗原因為: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功",HttpStatus.OK);
    }
}
