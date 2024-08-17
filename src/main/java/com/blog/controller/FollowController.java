package com.blog.controller;

import com.blog.response.ResponseBody;
import com.blog.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Follow", description = "關注API控制器相關功能")
@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {

//    private final FollowService followService;

    /**
     * 檢查該文章作者是否已被關注
     */
    @GetMapping("/checkIsFollowed/{followeeId}")
    @Operation(summary = "檢查該文章作者是否已被關注", description = "檢查該文章作者是否已被關注")
    public ResponseBody<Boolean> checkIsFollowed(@Parameter(name = "followeeId", description = "關注者ID", required = true) @PathVariable Long followeeId) {
        return null;
    }
}
