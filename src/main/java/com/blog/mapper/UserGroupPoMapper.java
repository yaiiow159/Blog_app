package com.blog.mapper;

import com.blog.dto.UserGroupDto;
import com.blog.po.UserGroupPo;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserGroupPoMapper extends BasicMapper {
    UserGroupPoMapper INSTANCE = Mappers.getMapper(UserGroupPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseFieldId")
    UserGroupPo partialUpdate(UserGroupDto userGroupDto, @MappingTarget UserGroupPo userGroupPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserGroupDto toDto(UserGroupPo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserGroupPo toPo(UserGroupDto dto);
    default List<UserGroupDto> toDtoList(List<UserGroupPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

}