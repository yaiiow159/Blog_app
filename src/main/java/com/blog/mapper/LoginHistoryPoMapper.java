package com.blog.mapper;

import com.blog.dto.LoginHistoryDto;
import com.blog.po.LoginHistoryPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginHistoryPoMapper {

    LoginHistoryPoMapper INSTANCE = Mappers.getMapper(LoginHistoryPoMapper.class);
    LoginHistoryPo toPo(LoginHistoryDto loginHistoryDto);

    LoginHistoryDto toDto(LoginHistoryPo loginHistoryPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    LoginHistoryPo partialUpdate(LoginHistoryDto loginHistoryDto, @MappingTarget LoginHistoryPo loginHistoryPo);
}