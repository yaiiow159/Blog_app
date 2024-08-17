package com.blog.mapper;

import com.blog.dto.RecentViewDto;
import com.blog.po.RecentViewPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {UserPoMapper.class})
public interface RecentViewPoMapper {

    RecentViewPoMapper INSTANCE = Mappers.getMapper(RecentViewPoMapper.class);
    RecentViewPo toPo(RecentViewDto recentViewDto);

    RecentViewDto toDto(RecentViewPo recentViewPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    RecentViewPo partialUpdate(RecentViewDto recentViewDto, @MappingTarget RecentViewPo recentViewPo);

}