package com.blog.dto;


import com.blog.po.UserPo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link UserPo}
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends BaseDto implements Serializable {

    @Schema(description = "使用者群組ID")
    private Long groupId;

    @Schema(description = "使用者群組名稱",example = "admin")
    private String groupName;

    @Schema(description = "使用者密碼",example = "admin1234",requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]{5,15}$",message = "密碼格式錯誤")
    private String password;

    @Schema(description = "電子郵件地址",example = "admin1234@example.com")
    @Email(message = "電子郵件格式錯誤",regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    private String email;

    @Schema(description = "是否鎖定",example = "false")
    private Boolean isLocked;

    @Schema(description = "使用者名稱",example = "admin",requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(description = "使用者暱稱",example = "admin")
    private String nickName;

    @Schema(description = "使用者地址",example = "台北市北投區北投里")
    private String address;

    @Schema(description = "使用者電話",example = "0912345678")
    private String phoneNumber;

    @Schema(description = "使用者角色")
    Set<RoleDto> roles;

    @Schema(description = "群組角色")
    UserGroupDto userGroupDto;

    @Schema(description = "使用者角色ID")
    List<Long> roleIds;

    @Schema(description = "使用者角色名稱")
    Set<String> roleNames;

}