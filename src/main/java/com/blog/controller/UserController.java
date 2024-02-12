    package com.blog.controller;

    import com.blog.dto.UserDto;
    import com.blog.dto.UserProfileDto;
    import com.blog.dto.UserProfileRequestBody;
    import com.blog.exception.ValidateFailedException;
    import com.blog.service.UserService;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.annotation.Resource;
    import jakarta.validation.Valid;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.Page;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import javax.naming.AuthenticationNotSupportedException;
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

    @GetMapping("/search/{id}")
    @Operation(summary = "查詢使用者",description = "使用id查詢使用者")
    public ResponseEntity<UserDto> searchUser(@Parameter(description = "使用者id",example = "1")@PathVariable long id){
         UserDto userDto = userService.findByUserId(id);
        if(userDto == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/searchBySpec")
    @Operation(summary = "動態條件查詢使用者",description = "動態條件查詢使用者")
    public ResponseEntity<Page<UserDto>> searchUserList(
                                                        @Parameter(description = "使用者名稱",example = "Timmy") @RequestParam(name = "userName") String userName,
                                                        @Parameter(description = "電子郵件",example = "Timmy123@yahoo.com") @RequestParam(name = "userEmail") String userEmail,
                                                        @Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "0") int page,
                                                        @Parameter(description = "筆數",example = "10" ) @RequestParam(name = "size",defaultValue = "10") int size,
                                                        @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                        @Parameter(description = "排序順序(正序/反序)",example = "ASC/DESC") @RequestParam(name = "direction",defaultValue = "ASC") String direction){
        Page<UserDto> userDtoPage = userService.findBySpec(userName,userEmail, page, size, sort, direction);
        return ResponseEntity.ok(userDtoPage);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "新增使用者API",description = "新增使用者API")
    public ResponseEntity<UserDto> createNewUser (
            @Parameter(name = "使用者帳戶") @Validated @RequestBody  UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/update")
    @Operation(summary = "更新使用者API",description = "更新使用者API")
    public ResponseEntity<UserDto> updateUser(@Parameter(name = "使用者帳戶") @Validated @RequestBody UserDto userDto) throws AuthenticationNotSupportedException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "刪除使用者API",description = "刪除使用者API")
    public ResponseEntity<String> deleteUser(@Parameter(description = "使用者id",example = "1") @PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(id));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/userProfile/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ResponseEntity<UserProfileDto> updateUserProfile(@Parameter(description = "使用者圖像",example = "avatar.png") @RequestPart("avatar") MultipartFile avatar,
                                                            @Parameter(description = "使用者名稱",example = "Timmy") @RequestPart("username") String username,
                                                            @Parameter(description = "使用者信箱",example = "Timmy123@gmail.com") @RequestPart("email") String email ,
                                                            @Parameter(description = "使用者暱稱",example = "Timmy") @RequestPart("nickname") String nickname,
                                                            @Parameter(description = "使用者生日",example = "2020/01/01") @RequestPart("birthday") String birthday,
                                                            @Parameter(description = "使用者地址",example = "台北市") @RequestPart("address") String address) throws IOException, ExecutionException, InterruptedException, TimeoutException {

        UserProfileRequestBody userProfileRequestBody = new UserProfileRequestBody();
        userProfileRequestBody.setAvatar(avatar);
        userProfileRequestBody.setName(username);
        userProfileRequestBody.setEmail(email);
        userProfileRequestBody.setNickName(nickname);
        userProfileRequestBody.setBirthday(birthday);
        userProfileRequestBody.setAddress(address);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserProfile(userProfileRequestBody));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/userProfile/get/{username}")
    @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @Parameter(description = "使用者名稱") @PathVariable String username) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile(username));
    }
}