package com.blog.mapper;

import com.blog.dto.TagDto;
import com.blog.po.TagPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagPoMapper {

    TagPoMapper INSTANCE = Mappers.getMapper(TagPoMapper.class);
    TagPo toPo(TagDto tagDto);

    TagDto toDto(TagPo tagPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TagPo partialUpdate(TagDto tagDto, @MappingTarget TagPo tagPo);

    default List<TagDto> toDtoList(List<TagPo> tagPos){
        return tagPos.stream().map(this::toDto).toList();
    }

    default Page<TagDto> toDtoPage(Page<TagPo> tagPos){
        return new PageImpl<>(toDtoList(tagPos.getContent()), tagPos.getPageable(), tagPos.getTotalElements());
    }
}