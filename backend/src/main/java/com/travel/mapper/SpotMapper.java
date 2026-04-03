package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Spot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpotMapper extends BaseMapper<Spot> {
}
