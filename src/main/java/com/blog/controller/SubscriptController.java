package com.blog.controller;


import com.blog.dto.ApiResponse;
import com.blog.dto.SubscriptReqBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "訂閱相關功能", description = "訂閱相關功能")
@RestController
@RequestMapping("/api/v1/subscript")
@RequiredArgsConstructor
public class SubscriptController {

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/notification")
    @Operation(summary = "訂閱通知", description = "訂閱通知", tags = {"訂閱相關功能"})
    public ApiResponse<String> subscribeNotification (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "訂閱該篇文章請求資料訊息", required = true)
            @Validated @RequestBody SubscriptReqBody subscriptReqBody) throws ResourceNotFoundException {
        String username = subscriptReqBody.getUsername();
        Long postId = subscriptReqBody.getPostId();
        String authorName = subscriptReqBody.getAuthorName();
        String email = subscriptReqBody.getEmail();
        try {
            subscriptionService.subscribe(username, postId, authorName, email);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "訂閱失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "訂閱成功", subscriptionService.subscribe(username, postId, authorName, email), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/notification")
    @Operation(summary = "取消訂閱通知", description = "取消訂閱通知", tags = {"訂閱相關功能"})
    public ApiResponse<String> cancelNotification (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "取消訂閱該篇文章請求資料訊息", required = true)
            @Validated @RequestBody SubscriptReqBody subscriptReqBody) {
       String username = subscriptReqBody.getUsername();
       Long postId = subscriptReqBody.getPostId();
       try {
           subscriptionService.unSubscribe(username, postId);
       } catch (Exception e) {
           e.printStackTrace();
           return new ApiResponse<>(false, "取消訂閱失敗", null, HttpStatus.BAD_REQUEST);
       }
       return new ApiResponse<>(true, "取消訂閱成功", subscriptionService.unSubscribe(username, postId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/checkSubscription")
    @Operation(summary = "檢查是否訂閱", description = "檢查是否訂閱", tags = {"訂閱相關功能"})
    public ApiResponse<String> checkSubscription(
            @Parameter(description = "使用者名稱", required = true) @RequestParam String username,
            @Parameter(description = "文章id", required = true) @RequestParam Long postId) {
        return new ApiResponse<>(true, "檢查成功", subscriptionService.checkSubscription(username, postId), HttpStatus.OK);
    }
}
