package com.blog.controller;


import com.blog.dto.SubscriptReqBody;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "訂閱相關功能", description = "訂閱相關功能")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class SubscriptController {

    private final SubscriptionService subscriptionService;
    @PostMapping("/notification/subscribe")
    @Operation(summary = "訂閱通知", description = "訂閱通知", tags = {"訂閱相關功能"})
    public ResponseEntity<String> subscribeNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "訂閱該篇文章請求資料訊息", required = true)
            @Validated @RequestBody SubscriptReqBody subscriptReqBody) throws ResourceNotFoundException {
        String username = subscriptReqBody.getUsername();
        Long postId = subscriptReqBody.getPostId();
        String authorName = subscriptReqBody.getAuthorName();
        String email = subscriptReqBody.getEmail();
        return ResponseEntity.ok(subscriptionService.subscribe(username, postId, authorName, email));
    }

    @PostMapping("/notification/cancel")
    @Operation(summary = "取消訂閱通知", description = "取消訂閱通知", tags = {"訂閱相關功能"})
    public ResponseEntity<String> cancelNotification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "取消訂閱該篇文章請求資料訊息", required = true)
            @Validated @RequestBody SubscriptReqBody subscriptReqBody) {
       String username = subscriptReqBody.getUsername();
       Long postId = subscriptReqBody.getPostId();
       return ResponseEntity.ok(subscriptionService.unSubscribe(username, postId));
    }

    @GetMapping("/checkSubscription")
    @Operation(summary = "檢查是否訂閱", description = "檢查是否訂閱", tags = {"訂閱相關功能"})
    public ResponseEntity<String> checkSubscription(
            @Parameter(description = "使用者名稱", required = true) @RequestParam String username,
            @Parameter(description = "文章id", required = true) @RequestParam Long postId) {
        return ResponseEntity.ok(subscriptionService.checkSubscription(username, postId));
    }
}
