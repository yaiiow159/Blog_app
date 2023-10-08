package com.blog.dto;

import com.blog.enumClass.ReviewLevel;
import com.blog.po.UserGroupPo;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.blog.po.UserGroupPo}
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserGroupDto extends BaseDto implements Serializable {
    public UserGroupDto (UserGroupDto userGroupDto){
        this.groupName = userGroupDto.groupName;
        this.description = userGroupDto.description;
        this.reviewLevel = userGroupDto.reviewLevel;
        this.userId = userGroupDto.userId;
    }

    @Schema(description = "群組名稱")
    @NotNull
    String groupName;

    @Schema(description = "群組描述")
    String description;

    @Schema(description = "覆核權限等級")
    ReviewLevel reviewLevel;

    @Schema(description = "使用者id")
    Long userId;
}