package com.blog.mapper;

import com.blog.dto.UserReportDto;
import com.blog.po.UserReportPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserPoMapper.class, CommentPoMapper.class})
public interface UserReportPoMapper extends BasicMapper {

    UserReportPoMapper INSTANCE = Mappers.getMapper(UserReportPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserReportPo toPo(UserReportDto userReportDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserReportDto toDto(UserReportPo userReportPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "reportTime", ignore = true)
    UserReportPo partialUpdate(UserReportDto userReportDto, @MappingTarget UserReportPo userReportPo);

    default List<UserReportDto> toDtoList(List<UserReportPo> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}