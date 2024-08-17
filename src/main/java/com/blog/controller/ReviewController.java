package com.blog.controller;

import com.blog.dto.UserReportDto;
import com.blog.response.ResponseBody;
import com.blog.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Tag(name = "覆核評論相關功能", description = "覆核評論相關功能")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "查詢所有使用者評論覆核資料", description = "查詢所有使用者評論覆核資料")
    public ResponseBody<Page<UserReportDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1" ) Integer page,
                                                     @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10" ) Integer pageSize,
                                                     @Parameter(description = "原因",example = "帶有惡意言論") @RequestParam(name = "reason",required = false) String reason,
                                                     @Parameter(description = "狀態",example = "pending/accept/reject") @RequestParam(name = "status",required = false) String status){
        Page<UserReportDto> userReportDtoPage;
        try {
            userReportDtoPage = reviewService.findAll(page,pageSize,reason,status);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", userReportDtoPage, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    @Operation(summary = "查詢所有使用者評論覆核資料", description = "查詢所有使用者評論覆核資料")
    public ResponseBody<List<UserReportDto>> findAll(){
        try {
            List<UserReportDto> userReportDtoList = reviewService.findAll();
            return new ResponseBody<>(true, "查詢成功", userReportDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "查詢使用者評論覆核資料",description = "查詢使用者評論覆核資料")
    public ResponseBody<UserReportDto> getReviewByUserId(@Parameter(description = "使用者id",example = "1") @PathVariable Long userId){
        UserReportDto userReportDto;
        try {
            userReportDto = reviewService.findByUserId(userId);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "查詢成功", userReportDto, HttpStatus.OK);
    }

    @GetMapping("/findByPending")
    @Operation(summary = "查詢所有使用者評論待覆核資料",description = "查詢所有使用者評論待覆核資料")
    public ResponseBody<List<UserReportDto>> findByPending(){
        try {
            List<UserReportDto> userReportDtoList = reviewService.findByStatusIsPending();
            return new ResponseBody<>(true, "查詢成功", userReportDtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/accept")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "接受使用者評論覆核資料",description = "接受使用者評論覆核資料")
    public ResponseBody<String> accept(@Parameter(description = "使用者id",example = "1") @RequestParam(name = "id") Long id){
        try {
            reviewService.accept(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "接受失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "接受成功", HttpStatus.OK);
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "拒絕使用者評論覆核資料",description = "拒絕使用者評論覆核資料")
    public ResponseBody<String> reject(@Parameter(description = "使用者id",example = "1")@RequestParam(name = "id") Long id){
        try {
            reviewService.reject(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "拒絕失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "拒絕成功", HttpStatus.OK);
    }

    @PostMapping("/batchAccept")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批次接受使用者評論覆核資料",description = "批次接受使用者評論覆核資料")
    public ResponseBody<String> batchAccept(@Parameter(description = "使用者id",example = "1,2,3") @RequestParam(name = "ids") List<Long> ids){
        try {
            reviewService.batchAccept(ids);
        } catch (Exception e) {
            return new ResponseBody<>(false, "批次接受失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "批次接受成功", HttpStatus.OK);
    }

    @PostMapping("/batchReject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批次拒絕使用者評論覆核資料",description = "批次拒絕使用者評論覆核資料")
    public ResponseBody<String> batchReject(@Parameter(description = "使用者id",example = "1,2,3") @RequestParam(name = "ids") List<Long> ids){
        try {
            reviewService.batchReject(ids);
        } catch (Exception e) {
            return new ResponseBody<>(false, "批次拒絕失敗, 失敗原因: " + e.getMessage(), null, HttpStatus.NO_CONTENT);
        }
        return new ResponseBody<>(true, "批次拒絕成功", HttpStatus.OK);
    }

}
