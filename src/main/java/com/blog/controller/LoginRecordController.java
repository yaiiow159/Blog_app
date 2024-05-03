package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.LoginHistoryDto;
import com.blog.service.LongRecordService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loginRecords")
@RequiredArgsConstructor
public class LoginRecordController {

    private final LongRecordService longRecordService;

    @GetMapping("{username}")
    public ApiResponse<Page<LoginHistoryDto>> getLoginRecords(@PathVariable(name = "username") String username,
                                                              @Parameter(name = "ipAddress") @RequestParam(name = "ipAddress" ,required = false) String ipAddress,
                                                              @Parameter(name = "action") @RequestParam(name = "action" ,required = false) String action,
                                                              @Parameter(name = "page") @RequestParam(name = "page" ,required = false) Integer page,
                                                              @Parameter(name = "size") @RequestParam(name = "pageSize" ,required = false) Integer pageSize) {
        Page<LoginHistoryDto> loginRecords = longRecordService.getLoginRecords(username,ipAddress,action,page,pageSize);
        if(loginRecords == null) {
            return new ApiResponse<>(false,"查無資料", HttpStatus.NO_CONTENT);
        }
        return new ApiResponse<>(true,"查詢成功",loginRecords,HttpStatus.OK);
    }

    @GetMapping("{username}/{id}")
    public ApiResponse<LoginHistoryDto> getLoginRecord(
            @PathVariable(name = "username") String username,
            @PathVariable Long id) {
        LoginHistoryDto loginRecord = longRecordService.getLoginRecord(id);
        if(ObjectUtils.isEmpty(loginRecord)) {
            return new ApiResponse<>(false,"查無資料",HttpStatus.NO_CONTENT);
        }
        return new ApiResponse<>(true,"查詢成功",loginRecord,HttpStatus.OK);
    }
}
