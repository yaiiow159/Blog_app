package com.blog.mapper;

import com.blog.dto.PostDto;
import com.blog.po.PostPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {CategoryPoMapper.class, TagPoMapper.class})
public interface PostPoMapper extends BasicMapper {

    PostPoMapper INSTANCE = Mappers.getMapper(PostPoMapper.class);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostPo toPo(PostDto postDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostDto toDto(PostPo postPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseEntityField")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    @Mapping(target = "bookmarks", ignore = true)
    PostPo partialUpdate(PostDto postDto, @MappingTarget PostPo postPo);

    default List<PostDto> toDtoList(List<PostPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}