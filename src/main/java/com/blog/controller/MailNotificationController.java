package com.blog.controller;

import com.blog.response.ResponseBody;
import com.blog.dto.MailNotificationDto;
import com.blog.service.MailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "郵件通知", description = "郵件通知相關API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class MailNotificationController {

    private final MailNotificationService mailNotificationService;

    @GetMapping("/{username}")
    @Operation(summary = "查詢所有郵件通知", description = "查詢所有郵件通知", tags = {"郵件通知"})
    public ResponseBody<Page<MailNotificationDto>> getAllMailNotification(
        @Parameter(description = "使用者名稱", example = "John") @PathVariable(name = "username") String username,
        @Parameter(description = "頁數", example = "1") @RequestParam(name = "page", defaultValue = "1") Integer page,
        @Parameter(description = "每頁筆數", example = "10") @RequestParam(name = "size", defaultValue = "10") Integer size,
        @Parameter(description = "姓名", example = "John") @RequestParam(name = "name", required = false) String name,
        @Parameter(description = "電子郵件", example = "pXbqF@example.com") @RequestParam(name = "email", required = false) String email,
        @Parameter(description = "主題", example = "Hello") @RequestParam(name = "subject", required = false) String subject,
        @Parameter(description = "是否已讀取", example = "true") @RequestParam(name = "isRead", required = false) Boolean isRead){

        Page<MailNotificationDto> mailNotificationDtoPage = mailNotificationService.getAllMailNotification(username,name, email, subject, isRead, page, size);
        return new ResponseBody<>(true, "查詢成功", mailNotificationDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{username}/{id}")
    @Operation(summary = "取得郵件通知", description = "取得郵件通知", tags = {"郵件通知"})
    public ResponseBody<MailNotificationDto> getMailNotification(@PathVariable(name = "id") Long id) {
        MailNotificationDto mailNotificationDto;
        try {
            mailNotificationDto = mailNotificationService.queryNotification(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查無資料", null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", mailNotificationDto, HttpStatus.OK);
    }

    @Operation(summary = "轉換郵件讀取狀態", description = "轉換郵件讀取狀態", tags = {"郵件通知"})
    @PutMapping
    public ResponseBody<String> updateMailNotification(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "郵件通知資訊" ,required = true)
                                                       @RequestBody @Validated MailNotificationDto mailNotificationDto){
        try {
            mailNotificationService.updateMailNotification(mailNotificationDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 失敗原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", null, HttpStatus.OK);
    }

    @Operation(summary = "取得目前郵件通知總數", description = "取得目前郵件通知總數", tags = {"郵件通知"})
    @GetMapping("/count")
    public ResponseBody<Long> getMailNotificationCount() {
        return new ResponseBody<>(true, "查詢成功", mailNotificationService.getMailNotificationCount(), HttpStatus.OK);
    }
}
