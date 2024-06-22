package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.UserReportDto;
import com.blog.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Slf4j
@Tag(name = "覆核評論相關功能", description = "覆核評論相關功能")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "查詢所有使用者評論覆核資料", description = "查詢所有使用者評論覆核資料")
    public ApiResponse<Page<UserReportDto>> findAll(@Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1" ) Integer page,
                                                    @Parameter(description = "每頁筆數",example = "10") @RequestParam(name = "pageSize",defaultValue = "10" ) Integer pageSize,
                                                    @Parameter(description = "原因",example = "帶有惡意言論") @RequestParam(name = "reason",required = false) String reason,
                                                    @Parameter(description = "狀態",example = "pending/accept/reject") @RequestParam(name = "status",required = false) Integer status){
        try {
            Page<UserReportDto> userReportDtoPage = reviewService.findAll(page, pageSize, reason, status);
            if (CollectionUtils.isEmpty(userReportDtoPage.getContent())) {
                return new ApiResponse<>(true, "暫無資料", null, HttpStatus.OK);
            } else {
                return new ApiResponse<>(true, "查詢成功", userReportDtoPage, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "錯誤原因" + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findList")
    @Operation(summary = "查詢所有使用者評論覆核資料", description = "查詢所有使用者評論覆核資料")
    public ApiResponse<List<UserReportDto>> findAll(){
        try {
            return new ApiResponse<>(true, "查詢成功", reviewService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ApiResponse<>(false, "錯誤原因" + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者評論覆核資料",description = "查詢使用者評論覆核資料")
    public ApiResponse<UserReportDto> getReviewByUserId(@Parameter(description = "使用者id",example = "1") @PathVariable Long id){
        try {
            UserReportDto userReportDto = reviewService.findById(id);
            return new ApiResponse<>(true, "查詢成功", userReportDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ApiResponse<>(false, "錯誤原因" + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findByPending")
    @Operation(summary = "查詢所有使用者評論待覆核資料",description = "查詢所有使用者評論待覆核資料")
    public ApiResponse<List<UserReportDto>> findByPending(){
        try {
            return new ApiResponse<>(true, "查詢成功", reviewService.findByStatusIsPending(), HttpStatus.OK);
        } catch (Exception e) {
            return new ApiResponse<>(false, "錯誤原因" + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/accept")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "接受使用者評論覆核資料",description = "接受使用者評論覆核資料")
    public ApiResponse<String> accept(@Parameter(description = "使用者id",example = "1") @RequestParam(name = "id") Long id){
        return new ApiResponse<>(true, "接受成功", reviewService.accept(id), HttpStatus.OK);
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "拒絕使用者評論覆核資料",description = "拒絕使用者評論覆核資料")
    public ApiResponse<String> reject(@Parameter(description = "使用者id",example = "1")@RequestParam(name = "id") Long id){
        return new ApiResponse<>(true, "拒絕成功", reviewService.reject(id), HttpStatus.OK);
    }

    @PostMapping("/batchAccept")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批次接受使用者評論覆核資料",description = "批次接受使用者評論覆核資料")
    public ApiResponse<String> batchAccept(@Parameter(description = "使用者id",example = "1,2,3") @RequestParam(name = "ids") List<Long> ids){
        return new ApiResponse<>(true, "批次接受成功", reviewService.batchAccept(ids), HttpStatus.OK);
    }

    @PostMapping("/batchReject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批次拒絕使用者評論覆核資料",description = "批次拒絕使用者評論覆核資料")
    public ApiResponse<String> batchReject(@Parameter(description = "使用者id",example = "1,2,3") @RequestParam(name = "ids") List<Long> ids){
        return new ApiResponse<>(true, "批次拒絕成功", reviewService.batchReject(ids), HttpStatus.OK);
    }

}
