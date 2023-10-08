package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {

    private UserDto userDto;
    private List<PostDto> postDtoList;
    private List<CommentDto> commentDtoList;
}
