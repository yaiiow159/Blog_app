package com.blog.controller;

import com.blog.dto.MailNotificationDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.MailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "郵件通知", description = "郵件通知相關API")
@RestController
@RequestMapping("/api/v1/mailNotification")
@RequiredArgsConstructor
public class MailNotificationController {

    private final MailNotificationService mailNotificationService;

    @GetMapping
    @Operation(summary = "查詢所有郵件通知", description = "查詢所有郵件通知", tags = {"郵件通知"})
    public ResponseEntity<Page<MailNotificationDto>> getAllMailNotification(
        @Parameter(description = "頁數", example = "0") @RequestParam(name = "page", defaultValue = "1") Integer page,
        @Parameter(description = "每頁筆數", example = "10") @RequestParam(name = "size", defaultValue = "10") Integer size,
        @Parameter(description = "排序", example = "id") @RequestParam(name = "sort", defaultValue = "id") String sort,
        @Parameter(description = "排序方向", example = "asc") @RequestParam(name = "direction", defaultValue = "asc") String direction,
        @Parameter(description = "姓名", example = "John") @RequestParam(name = "name", required = false) String name,
        @Parameter(description = "電子郵件", example = "pXbqF@example.com") @RequestParam(name = "email", required = false) String email,
        @Parameter(description = "主題", example = "Hello") @RequestParam(name = "subject", required = false) String subject,
        @Parameter(description = "是否已讀取", example = "true") @RequestParam(name = "isRead", required = false) Boolean isRead){
        List<MailNotificationDto> mailNotificationDtoList = mailNotificationService.getAllMailNotification(name, email,subject, isRead, page, size, sort, direction).getContent();
        if(CollectionUtils.isEmpty(mailNotificationDtoList)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mailNotificationService.getAllMailNotification(name, email, subject, isRead, page, size, sort, direction));
    }

    @Operation(summary = "轉換郵件讀取狀態", description = "轉換郵件讀取狀態", tags = {"郵件通知"})
    @PostMapping("/update")
    public ResponseEntity<String> updateMailNotification(@Validated @RequestBody MailNotificationDto mailNotificationDto) throws ResourceNotFoundException {
        return ResponseEntity.ok(mailNotificationService.updateMailNotification(mailNotificationDto));
    }

    @Operation(summary = "取得目前郵件通知總數", description = "取得目前郵件通知總數", tags = {"郵件通知"})
    @GetMapping("/count")
    public ResponseEntity<Long> getMailNotificationCount() {
        return ResponseEntity.ok(mailNotificationService.getMailNotificationCount());
    }
}
