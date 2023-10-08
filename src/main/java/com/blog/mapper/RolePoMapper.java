package com.blog.mapper;

import com.blog.dto.RoleDto;
import com.blog.po.RolePo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {RolePoMapper.class})
public interface RolePoMapper {
    RolePoMapper INSTANCE = Mappers.getMapper(RolePoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RolePo partialUpdate(RoleDto roleDto, @MappingTarget RolePo rolePo);

    RoleDto toDto(RolePo entity);

    RolePo toPo(RoleDto dto);
    default List<RoleDto> toDtoList(List<RolePo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<RolePo> toPoList(List<RoleDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }
}