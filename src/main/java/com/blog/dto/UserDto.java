package com.blog.dto;


import com.blog.po.UserPo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link UserPo}
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto implements Serializable {

    @Schema(description = "使用者群組")
    private UserGroupDto userGroupDto;

    @Schema(description = "使用者群組名稱",example = "admin")
    private String groupName;

    @Schema(description = "使用者密碼",example = "admin1234")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,16}$")
    String password;

    @Schema(description = "電子郵件地址",example = "admin1234@example.com")
    @Email(message = "電子郵件格式錯誤")
    String email;

    @Schema(description = "使用者名稱",example = "admin")
    private String userName;

    @Schema(description = "生日",example = "2022-01-01 00:00:00")
    private LocalDate birthday;

    @Schema(description = "使用者暱稱",example = "admin")
    private String nickName;

    @Schema(description = "使用者地址",example = "台北市北投區北投里")
    private String address;

    @Schema(description = "使用者電話",example = "0912345678")
    private String phone;

    @Schema(description = "使用者角色")
    Set<RoleDto> roles;

    @Schema(description = "使用者角色名稱",example = "admin")
    Set<String> rolesName;
}