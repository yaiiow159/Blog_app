package com.blog.mapper;

import com.blog.dto.PostDto;
import com.blog.po.PostPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostPoMapper {

    PostPoMapper INSTANCE = Mappers.getMapper(PostPoMapper.class);
    PostPo toPo(PostDto postDto);

    PostDto toDto(PostPo postPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostPo partialUpdate(PostDto postDto, @MappingTarget PostPo postPo);

    default List<PostDto> toDtoList(List<PostPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<PostPo> toPoList(List<PostDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }

    default Page<PostDto> toDtoPage(Page<PostPo> pageEntity) {
        List<PostDto> dtos = toDtoList(pageEntity.getContent());
        return new PageImpl<>(dtos, pageEntity.getPageable(), pageEntity.getTotalElements());
    }

    default Page<PostPo> toPoPage(Page<PostDto> pageDto) {
        List<PostPo> entities = toPoList(pageDto.getContent());
        return new PageImpl<>(entities, pageDto.getPageable(), pageDto.getTotalElements());
    }
}