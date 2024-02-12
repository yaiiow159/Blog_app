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
public interface UserPoMapper {
    UserPoMapper INSTANCE = Mappers.getMapper(UserPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserPo partialUpdate(UserDto userDto, @MappingTarget UserPo userPo);

    UserDto toDto(UserPo entity);

    UserPo toPo(UserDto dto);

    default List<UserDto> toDtoList(List<UserPo> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<UserPo> toPoList(List<UserDto> dtoList) {
        return dtoList.stream().map(this::toPo).collect(Collectors.toList());
    }

    default Page<UserDto> toDtoPage(Page<UserPo> pageEntity) {
        List<UserDto> dtos = toDtoList(pageEntity.getContent());
        return new PageImpl<>(dtos, pageEntity.getPageable(), pageEntity.getTotalElements());
    }

    default Page<UserPo> toPoPage(Page<UserDto> pageDto) {
        List<UserPo> entities = toPoList(pageDto.getContent());
        return new PageImpl<>(entities, pageDto.getPageable(), pageDto.getTotalElements());
    }
}