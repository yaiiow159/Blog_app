package com.blog.mapper;

import com.blog.dto.CommentDto;
import com.blog.po.CommentPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentPoMapper extends BasicMapper {
    CommentPoMapper INSTANCE = Mappers.getMapper(CommentPoMapper.class);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommentPo toPo(CommentDto commentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommentDto toDto(CommentPo commentPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseEntityField")
    CommentPo partialUpdate(CommentDto commentDto, @MappingTarget CommentPo commentPo);

    default List<CommentDto> toDtoList(List<CommentPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<CommentPo> toPoList(List<CommentDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }
}