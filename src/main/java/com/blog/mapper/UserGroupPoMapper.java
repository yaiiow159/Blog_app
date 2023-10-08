package com.blog.mapper;

import com.blog.dto.UserGroupDto;
import com.blog.enumClass.ReviewLevel;
import com.blog.po.UserGroupPo;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserGroupPoMapper {
    UserGroupPoMapper INSTANCE = Mappers.getMapper(UserGroupPoMapper.class);

    default ReviewLevel map(Object value) {
        return ReviewLevel.valueOf(value.toString());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserGroupPo partialUpdate(UserGroupDto userGroupDto, @MappingTarget UserGroupPo userGroupPo);

    UserGroupDto toDto(UserGroupPo entity);

    UserGroupPo toPo(UserGroupDto dto);
    default List<UserGroupDto> toDtoList(List<UserGroupPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<UserGroupPo> toPoList(List<UserGroupDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }
}