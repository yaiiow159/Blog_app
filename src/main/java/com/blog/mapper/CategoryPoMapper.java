package com.blog.mapper;

import com.blog.dto.CategoryDto;
import com.blog.po.CategoryPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryPoMapper {
    CategoryPoMapper INSTANCE = Mappers.getMapper(CategoryPoMapper.class);
    CategoryPo toPo(CategoryDto categoryDto);

    CategoryDto toDto(CategoryPo categoryPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CategoryPo partialUpdate(CategoryDto categoryDto, @MappingTarget CategoryPo categoryPo);
}