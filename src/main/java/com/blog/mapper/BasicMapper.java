package com.blog.mapper;

import com.blog.dto.BaseDto;
import com.blog.po.BasicPo;
import io.lettuce.core.support.BasePool;
import org.mapstruct.*;

@Mapper
public interface BasicMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updDate", ignore = true)
    @Mapping(target = "creatUser", ignore = true)
    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "id", ignore = true)
    BasicPo ignoreBaseEntityField(BaseDto baseDto, @MappingTarget BasicPo basicPo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    BasicPo ignoreBaseFieldId(BaseDto baseDto, @MappingTarget BasicPo basicPo);

}
