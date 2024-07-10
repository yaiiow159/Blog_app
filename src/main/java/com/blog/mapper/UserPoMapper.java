package com.blog.mapper;

import com.blog.dto.UserDto;
import com.blog.po.UserPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {RolePoMapper.class, UserGroupPoMapper.class})
public interface UserPoMapper extends BasicMapper {
    UserPoMapper INSTANCE = Mappers.getMapper(UserPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "ignoreBaseFieldId")
    UserPo partialUpdate(UserDto userDto, @MappingTarget UserPo userPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserDto toDto(UserPo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserPo toPo(UserDto dto);

    default List<UserDto> toDtoList(List<UserPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<UserPo> toPoList(List<UserDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }
}