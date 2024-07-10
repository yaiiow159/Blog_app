package com.blog.mapper;

import com.blog.dto.SubscriptionDto;
import com.blog.po.SubscriptionPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserPoMapper.class, PostPoMapper.class})
public interface SubscriptionPoMapper {
    SubscriptionPoMapper INSTANCE = Mappers.getMapper(SubscriptionPoMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SubscriptionPo toPo(SubscriptionDto subscriptionDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SubscriptionDto toDto(SubscriptionPo subscriptionPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post", ignore = true)
    SubscriptionPo partialUpdate(SubscriptionDto subscriptionDto, @MappingTarget SubscriptionPo subscriptionPo);

    default List<SubscriptionDto> toDtoList(List<SubscriptionPo> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}