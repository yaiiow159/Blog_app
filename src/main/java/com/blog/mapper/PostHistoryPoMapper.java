package com.blog.mapper;

import com.blog.dto.PostHistoryPoDto;
import com.blog.po.PostHistoryPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostHistoryPoMapper {
    PostHistoryPoMapper INSTANCE = Mappers.getMapper(PostHistoryPoMapper.class);
    PostHistoryPo toPo(PostHistoryPoDto postHistoryPoDto);

    PostHistoryPoDto toDto(PostHistoryPo postHistoryPo);

    List<PostHistoryPoDto> toDtoList(List<PostHistoryPo> postHistoryPos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostHistoryPo partialUpdate(PostHistoryPoDto postHistoryPoDto, @MappingTarget PostHistoryPo postHistoryPo);
}