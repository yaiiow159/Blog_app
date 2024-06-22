package com.blog.mapper;

import com.blog.dto.UserReportDto;
import com.blog.po.UserReportPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserReportPoMapper {

    UserReportPoMapper INSTANCE = Mappers.getMapper(UserReportPoMapper.class);
    UserReportPo toPo(UserReportDto userReportDto);

    UserReportDto toDto(UserReportPo userReportPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserReportPo partialUpdate(UserReportDto userReportDto, @MappingTarget UserReportPo userReportPo);

    default List<UserReportDto> toDtoList(List<UserReportPo> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    default List<UserReportPo> toPoList(List<UserReportDto> dtos) {
        return dtos.stream().map(this::toPo).toList();
    }

    default Page<UserReportDto> toDtoPage(Page<UserReportPo> poPage) {
        return poPage.map(this::toDto);
    }

    default Page<UserReportPo> toPoPage(Page<UserReportDto> dtoPage) {
        return dtoPage.map(this::toPo);
    }
}