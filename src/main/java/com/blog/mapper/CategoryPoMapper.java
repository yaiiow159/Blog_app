package com.blog.mapper;

import com.blog.dto.CategoryDto;
import com.blog.po.CategoryPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryPoMapper {
    CategoryPoMapper INSTANCE = Mappers.getMapper(CategoryPoMapper.class);
    CategoryPo toPo(CategoryDto categoryDto);

    CategoryDto toDto(CategoryPo categoryPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CategoryPo partialUpdate(CategoryDto categoryDto, @MappingTarget CategoryPo categoryPo);

    default List<CategoryDto> toDtoList(List<CategoryPo> content){
        return content.stream().map(this::toDto).toList();
    }
    default Page<CategoryDto> toDtoPage(Page<CategoryPo> content){
        return new PageImpl<>(toDtoList(content.getContent()), content.getPageable(), content.getTotalElements());
    }
}