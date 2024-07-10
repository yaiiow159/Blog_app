package com.blog.controller;

import com.blog.response.ResponseBody;
import com.blog.service.CommonService;
import com.blog.vo.UserCommentLikeVo;
import com.blog.vo.UserPostLikeCountVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通用API", description = "通用功能相關API")
@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/userCommentLikeCount/{username}")
    @Operation(summary = "查詢使用者評論按讚數", description = "查詢使用者評論按讚數")
    public ResponseBody<UserCommentLikeVo> getUserCommentLikeCount(@Parameter(description = "使用者名稱", required = true) @PathVariable String username) {
        return new ResponseBody<>(true, "查詢成功", commonService.getUserCommentLikeCount(username), HttpStatus.OK);
    }

    @GetMapping("/userPostLikeCount/{username}")
    @Operation(summary = "查詢使用者文章按讚數", description = "查詢使用者文章按讚數")
    public ResponseBody<UserPostLikeCountVo> getUserLikeCount(@Parameter(description = "使用者名稱", required = true) @PathVariable String username) {
        return new ResponseBody<>(true, "查詢成功", commonService.getUserPostLikeCount(username), HttpStatus.OK);
    }
}
