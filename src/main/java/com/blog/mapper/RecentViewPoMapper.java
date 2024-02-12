package com.blog.mapper;

import com.blog.po.RecentViewPo;
import com.blog.dto.RecentViewPoDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {UserPoMapper.class})
public interface RecentViewPoMapper {

    RecentViewPoMapper INSTANCE = Mappers.getMapper(RecentViewPoMapper.class);
    RecentViewPo toPo(RecentViewPoDto recentViewPoDto);

    RecentViewPoDto toDto(RecentViewPo recentViewPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RecentViewPo partialUpdate(RecentViewPoDto recentViewPoDto, @MappingTarget RecentViewPo recentViewPo);

}