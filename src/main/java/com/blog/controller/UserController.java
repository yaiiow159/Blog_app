    package com.blog.controller;

    import com.blog.annotation.NoResubmit;
    import com.blog.dto.ApiResponse;
    import com.blog.dto.UserDto;
    import com.blog.dto.UserProfileDto;
    import com.blog.service.UserService;


    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.annotation.Resource;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.Page;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.util.CollectionUtils;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.util.concurrent.ExecutionException;
    import java.util.concurrent.TimeoutException;

@Tag(name = "使用者相關功能", description = "使用者相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "查詢使用者",description = "使用id查詢使用者")
    public ApiResponse<UserDto> searchUser(@Parameter(description = "使用者id",example = "1")@PathVariable Long id){
         UserDto userDto = userService.findByUserId(id);
        if(userDto == null)
           return new ApiResponse<>(false, "查無此使用者", HttpStatus.NOT_FOUND);
        return new ApiResponse<>(true, "查詢成功", userDto, HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "動態條件查詢使用者",description = "動態條件查詢使用者")
    public ApiResponse<Page<UserDto>> findAll(
                                                        @Parameter(description = "使用者名稱",example = "Timmy") @RequestParam(name = "name") String userName,
                                                        @Parameter(description = "電子郵件",example = "Timmy123@yahoo.com") @RequestParam(name = "email") String userEmail,
                                                        @Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1") Integer page,
                                                        @Parameter(description = "筆數",example = "10" ) @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize) {
        Page<UserDto> userDtoPage = userService.findAll(userName,userEmail, page, pageSize);
        if(CollectionUtils.isEmpty(userDtoPage.getContent()))
            return new ApiResponse<>(false, "查無此使用者", HttpStatus.NOT_FOUND);
        return new ApiResponse<>(true, "查詢成功", userDtoPage, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/upload")
    @Operation(summary = "上傳圖片",description = "上傳圖片")
    public ApiResponse<String> uploadProfileImage(@RequestParam("file") MultipartFile file){
        try {
            userService.upload(file);
        } catch (Exception e) {
            return new ApiResponse<>(false, "上傳失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "上傳成功",  HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "新增使用者API",description = "新增使用者API")
    public ApiResponse<UserDto> createNewUser (
            @Parameter(name = "使用者帳戶") @Validated @RequestBody UserDto userDto) {
        try {
            userService.add(userDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "新增失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "新增成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新使用者API",description = "更新使用者API")
    public ApiResponse<UserDto> updateUser(@Parameter(name = "使用者帳戶") @Validated @RequestBody UserDto userDto) {
        try {
            userService.edit(userDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "更新失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除使用者API",description = "刪除使用者API")
    public ApiResponse<String> deleteUser(@Parameter(description = "使用者id",example = "1") @PathVariable long id){
        try {
            userService.delete(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "刪除失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "刪除成功", HttpStatus.OK);
    }


    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/locked/{id}")
    @Operation(summary = "鎖戶使用者",description = "鎖戶使用者")
    public ApiResponse<String> bannedUser(@Parameter(description = "使用者ID",example = "1") @PathVariable Long id){
        try {
            userService.lockUser(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "鎖戶失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "鎖戶成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unlocked/{id}")
    @Operation(summary = "解鎖使用者",description = "解鎖使用者")
    public ApiResponse<String> unbannedUser(@Parameter(description = "使用者ID",example = "1") @PathVariable Long id){
        try {
            userService.unlockUser(id);
        } catch (Exception e) {
            return new ApiResponse<>(false, "解鎖失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "解鎖成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping(value = "/userProfile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ApiResponse<UserProfileDto> updateUserProfile(@Validated @RequestBody UserProfileDto userProfileDto) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        userService.updateUserProfile(userProfileDto);
        if(userProfileDto == null)
            return new ApiResponse<>(false, "更新個人資料失敗",HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "更新個人資料成功", userProfileDto,HttpStatus.OK);
    }

    @GetMapping("/userProfile/{username}")
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ApiResponse<UserProfileDto> getUserProfile(
            @Parameter(description = "使用者名稱") @PathVariable String username) throws Exception {
        UserProfileDto userProfile = userService.getUserProfile(username);
        if(userProfile == null)
            return new ApiResponse<>(false, "查無資料", null,HttpStatus.NO_CONTENT);
        return new ApiResponse<>(true, "查詢成功",userProfile,HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/changePassword")
    @Operation(summary = "修改密碼API",description = "修改密碼API")
    public ApiResponse<String> changePassword(
            @Parameter(description = "原密碼") @RequestParam String oldPassword,
            @Parameter(description = "新密碼") @RequestParam String newPassword) {
        try {
            userService.changePassword(oldPassword, newPassword);
        } catch (Exception e) {
            return new ApiResponse<>(false, "修改密碼失敗 原因: " + e.getMessage(), "", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "修改密碼成功",HttpStatus.OK);
    }

}