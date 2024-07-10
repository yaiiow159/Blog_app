package com.blog.mapper;

import com.blog.dto.RoleDto;
import com.blog.po.RolePo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolePoMapper extends BasicMapper {
    RolePoMapper INSTANCE = Mappers.getMapper(RolePoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseFieldId")
    RolePo partialUpdate(RoleDto roleDto, @MappingTarget RolePo rolePo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoleDto toDto(RolePo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RolePo toPo(RoleDto dto);
    default List<RoleDto> toDtoList(List<RolePo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<RolePo> toPoList(List<RoleDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }
}