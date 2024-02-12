package com.blog.mapper;

import com.blog.dto.SubscriptionDto;
import com.blog.po.SubscriptionPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserPoMapper.class, PostPoMapper.class})
public interface SubscriptionPoMapper {
    SubscriptionPoMapper INSTANCE = Mappers.getMapper(SubscriptionPoMapper.class);
    SubscriptionPo toPo(SubscriptionDto subscriptionDto);

    SubscriptionDto toDto(SubscriptionPo subscriptionPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SubscriptionPo partialUpdate(SubscriptionDto subscriptionDto, @MappingTarget SubscriptionPo subscriptionPo);

    List<SubscriptionDto> toDtoList(List<SubscriptionPo> subscriptionPoList);
}