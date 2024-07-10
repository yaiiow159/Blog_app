    package com.blog.controller;

    import com.blog.annotation.NoResubmit;
    import com.blog.exception.ResourceNotFoundException;
    import com.blog.response.ResponseBody;
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
    @Operation(summary = "基於使用者id查詢使用者",description = "使用id查詢使用者")
    public ResponseBody<UserDto> searchUser(@Parameter(description = "使用者id",example = "1")@PathVariable Long id) {
        UserDto userDto;
        try {
            userDto = userService.findById(id);
            if(null == userDto)
                return new ResponseBody<>(false, "查無此使用者", HttpStatus.NOT_FOUND);
            return new ResponseBody<>(true, "查詢成功", userDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "新增時遭遇錯誤 錯誤原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "動態條件查詢使用者",description = "動態條件查詢使用者")
    public ResponseBody<Page<UserDto>> findAll(@Parameter(description = "使用者名稱",example = "Timmy") @RequestParam(name = "name") String userName,
                                               @Parameter(description = "電子郵件",example = "Timmy123@yahoo.com") @RequestParam(name = "email") String userEmail,
                                               @Parameter(description = "頁數",example = "1") @RequestParam(name = "page",defaultValue = "1") Integer page,
                                               @Parameter(description = "筆數",example = "10" ) @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize) {
        Page<UserDto> userDtoPage;
        try {
            userDtoPage = userService.findAll(page, pageSize,userName,userEmail);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢時遭遇錯誤 錯誤原因為: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "查詢成功", userDtoPage, HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "新增使用者",description = "新增使用者")
    public ResponseBody<UserDto> createNewUser (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "使用者資訊") @Validated @RequestBody UserDto userDto) {
        try {
            userService.save(userDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "新增失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "新增成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    @Operation(summary = "更新使用者API",description = "更新使用者API")
    public ResponseBody<UserDto> updateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "使用者資訊") @Validated @RequestBody UserDto userDto) {
        try {
            userService.update(userDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除使用者API",description = "刪除使用者API")
    public ResponseBody<String> deleteUser(@Parameter(description = "使用者id",example = "1") @PathVariable Long id){
        try {
            userService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/locked/{id}")
    @Operation(summary = "鎖戶使用者",description = "鎖戶使用者")
    public ResponseBody<String> bannedUser(@Parameter(description = "使用者ID",example = "1") @PathVariable Long id){
        try {
            userService.lock(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "鎖戶失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "鎖戶成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unlocked/{id}")
    @Operation(summary = "解鎖使用者",description = "解鎖使用者")
    public ResponseBody<String> unbannedUser(@Parameter(description = "使用者ID",example = "1") @PathVariable Long id){
        try {
            userService.unlock(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "解鎖失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "解鎖成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping(value = "/userProfile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ResponseBody<UserProfileDto> updateUserProfile(@RequestBody @Validated UserProfileDto userProfileDto) {
        try {
            userService.updateProfile(userProfileDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @GetMapping("/userProfile/{username}")
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ResponseBody<UserProfileDto> getUserProfile(
            @Parameter(description = "使用者名稱") @PathVariable String username){
        UserProfileDto userProfileDto;
        try {
            userProfileDto = userService.queryProfile(username);
            if(userProfileDto == null){
                return new ResponseBody<>(false, "查詢失敗 原因: 使用者不存在", null, HttpStatus.NOT_FOUND);
            }
            return new ResponseBody<>(true, "更新成功",userProfileDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗 原因: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/changePassword")
    @Operation(summary = "修改密碼API",description = "修改密碼API")
    public ResponseBody<String> changePassword(
            @Parameter(description = "原密碼") @RequestParam String oldPassword,
            @Parameter(description = "新密碼") @RequestParam String newPassword) {
        try {
            userService.changePassword(oldPassword, newPassword);
        } catch (Exception e) {
            return new ResponseBody<>(false, "修改密碼失敗 原因: " + e.getMessage(), "", HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "修改密碼成功",HttpStatus.OK);
    }

}