package com.blog.mapper;

import com.blog.dto.MailNotificationDto;
import com.blog.po.MailNotificationPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MailNotificationPoMapper {

    MailNotificationPoMapper INSTANCE = Mappers.getMapper(MailNotificationPoMapper.class);
    MailNotificationPo toPo(MailNotificationDto mailNotificationDto);

    MailNotificationDto toDto(MailNotificationPo mailNotificationPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MailNotificationPo partialUpdate(MailNotificationDto mailNotificationDto, @MappingTarget MailNotificationPo mailNotificationPo);

    List<MailNotificationDto> toDtoList(List<MailNotificationPo> mailNotificationPoList);

    List<MailNotificationPo> toPoList(List<MailNotificationDto> mailNotificationDtoList);
}