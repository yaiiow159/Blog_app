package com.blog.dto;

import com.blog.po.RolePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.io.Serializable;
;

/**
 * DTO for {@link RolePo}
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleDto extends BaseDto implements Serializable {

    @NonNull
    String roleName;
}