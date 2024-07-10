package com.blog.controller;


import com.blog.dto.SubscriptReqBody;
import com.blog.response.ResponseBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "訂閱相關功能", description = "訂閱相關功能")
@RestController
@RequestMapping("/api/v1/subscript")
@RequiredArgsConstructor
public class SubscriptController {

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/notification")
    @Operation(summary = "訂閱通知", description = "訂閱通知", tags = {"訂閱相關功能"})
    public ResponseBody<String> subscribeNotification (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "訂閱該篇文章請求資料訊息", required = true)
            @RequestBody @Validated SubscriptReqBody subscriptReqBody) {
        String username = subscriptReqBody.getUsername();
        Long postId = subscriptReqBody.getPostId();
        String authorName = subscriptReqBody.getAuthorName();
        String email = subscriptReqBody.getEmail();
        try {
            subscriptionService.subscribe(username, postId, authorName, email);
        } catch (Exception e) {
            return new ResponseBody<>(false, "訂閱失敗 原因:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "訂閱成功", null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/notification")
    @Operation(summary = "取消訂閱通知", description = "取消訂閱通知", tags = {"訂閱相關功能"})
    public ResponseBody<String> cancelNotification (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "取消訂閱該篇文章請求資料訊息", required = true)
            @RequestBody @Validated SubscriptReqBody subscriptReqBody) {
       String username = subscriptReqBody.getUsername();
       Long postId = subscriptReqBody.getPostId();
        try {
            subscriptionService.unSubscribe(username, postId);
        } catch (Exception e) {
            return new ResponseBody<>(false, "取消訂閱失敗 原因:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "取消訂閱成功", null, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/checkSubscription")
    @Operation(summary = "檢查是否訂閱", description = "檢查是否訂閱", tags = {"訂閱相關功能"})
    public ResponseBody<Boolean> checkSubscription(
            @Parameter(description = "使用者名稱", required = true) @RequestParam String username,
            @Parameter(description = "文章id", required = true) @RequestParam Long postId) {
        boolean isSubscribed = subscriptionService.checkSubscription(username, postId);
        return new ResponseBody<>(true, null, isSubscribed, HttpStatus.OK);
    }
}
