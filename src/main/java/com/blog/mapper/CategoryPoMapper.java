package com.blog.mapper;

import com.blog.dto.CategoryDto;
import com.blog.po.CategoryPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper
public interface CategoryPoMapper extends BasicMapper {
    CategoryPoMapper INSTANCE = Mappers.getMapper(CategoryPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CategoryPo toPo(CategoryDto categoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CategoryDto toDto(CategoryPo categoryPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseEntityField")
    CategoryPo partialUpdate(CategoryDto categoryDto, @MappingTarget CategoryPo categoryPo);

    default List<CategoryDto> toDtoList(List<CategoryPo> content){
        return content.stream().map(this::toDto).toList();
    }
}