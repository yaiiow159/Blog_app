    package com.blog.controller;

    import com.blog.dto.UserDto;
    import com.blog.dto.UserProfileDto;
    import com.blog.dto.UserProfileRequestBody;
    import com.blog.exception.ResourceNotFoundException;
    import com.blog.exception.ValidateFailedException;
    import com.blog.service.UserService;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.Page;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.util.CollectionUtils;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import javax.annotation.Resource;
    import javax.naming.AuthenticationNotSupportedException;

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
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }

        @GetMapping("/searchBySpec")
        @Operation(summary = "動態條件查詢使用者",description = "動態條件查詢使用者")
        public ResponseEntity<Page<UserDto>> searchUserList(@Parameter(description = "使用者編號",example = "1") @RequestParam(name = "id",required = false) Long id,
                                                            @Parameter(description = "使用者名稱",example = "Timmy") @RequestParam(name = "userName") String userName,
                                                            @Parameter(description = "電子郵件",example = "Timmy123@yahoo.com") @RequestParam(name = "userEmail") String userEmail,
                                                            @Parameter(description = "頁數",example = "0") @RequestParam(name = "page",defaultValue = "0") int page,
                                                            @Parameter(description = "筆數",example = "10" ) @RequestParam(name = "size",defaultValue = "10") int size,
                                                            @Parameter(description = "排序",example = "id") @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                            @Parameter(description = "排序順序(正序/反序)",example = "ASC/DESC") @RequestParam(name = "direction",defaultValue = "ASC") String direction){
            Page<UserDto> userDtoList = userService.findBySpec(id,userName,userEmail, page, size, sort, direction);
            return new ResponseEntity<>(userDtoList, HttpStatus.OK);
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PostMapping("/create")
        @Operation(summary = "新增使用者API",description = "新增使用者API")
        public ResponseEntity<UserDto> createNewUser (
                @Parameter(name = "使用者帳戶") @Validated @RequestBody UserDto userDto) throws AuthenticationNotSupportedException, ValidateFailedException {
            return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.OK);
        }

        @PreAuthorize("hasRole('ROLE_USER')")
        @PutMapping("/update")
        @Operation(summary = "更新使用者API",description = "更新使用者API")
        public ResponseEntity<UserDto> updateUser(@Parameter(name = "使用者帳戶") @Validated @RequestBody UserDto userDto) throws AuthenticationNotSupportedException {
            return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @DeleteMapping("/delete/{id}")
        @Operation(summary = "刪除使用者API",description = "刪除使用者API")
        public ResponseEntity<String> deleteUser(@Parameter(description = "使用者id",example = "1") @PathVariable long id){
            return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
        }

        @PreAuthorize("hasRole('ROLE_USER')")
        @PostMapping("/userProfile")
        @Operation(summary = "使用者個人資料API",description = "使用者個人資料API")
        public ResponseEntity<UserProfileDto> userProfile(@Parameter(description = "使用者用戶資料") @Validated @RequestBody UserProfileRequestBody userProfileRequestBody) throws ResourceNotFoundException {
            return new ResponseEntity<>(userService.findUserProfileByUserNameOrEmail(userProfileRequestBody), HttpStatus.OK);
        }

    }