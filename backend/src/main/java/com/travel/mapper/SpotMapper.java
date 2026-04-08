package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Spot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SpotMapper extends BaseMapper<Spot> {
    @Select("SELECT * FROM spots WHERE deleted = 0 AND status = 1 AND (name LIKE CONCAT('%', #{keyword}, '%') OR city LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%')) ORDER BY hot_score DESC LIMIT #{limit}")
    List<Spot> selectSpotByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
}
