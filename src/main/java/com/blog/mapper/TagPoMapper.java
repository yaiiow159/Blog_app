package com.blog.mapper;

import com.blog.dto.TagDto;
import com.blog.po.TagPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {PostPoMapper.class})
public interface TagPoMapper extends BasicMapper{

    TagPoMapper INSTANCE = Mappers.getMapper(TagPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TagPo toPo(TagDto tagDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TagDto toDto(TagPo tagPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseEntityField")
    TagPo partialUpdate(TagDto tagDto, @MappingTarget TagPo tagPo);

    default List<TagDto> toDtoList(List<TagPo> tagPos){
        return tagPos.stream().map(this::toDto).toList();
    }

}