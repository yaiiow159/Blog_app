package com.blog.dto;


import com.blog.po.UserPo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link UserPo}
 */

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserDto extends BaseDto implements Serializable {

    @Schema(description = "使用者群組")
    private UserGroupDto userGroupDto;

    @Schema(description = "使用者密碼",example = "admin1234")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,16}$")
    String password;

    @Schema(description = "電子郵件地址",example = "admin1234@example.com")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
    String email;

    @Schema(description = "使用者名稱",example = "admin")
    private String userName;

    @Valid
    @Schema(description = "使用者權限")
    Set<RoleDto> roles;
}