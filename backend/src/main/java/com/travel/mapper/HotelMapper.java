package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Hotel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {
    @Select("SELECT * FROM hotels WHERE deleted = 0 AND status = 1 AND (name LIKE CONCAT('%', #{keyword}, '%') OR city LIKE CONCAT('%', #{keyword}, '%')) ORDER BY hot_score DESC LIMIT #{limit}")
    List<Hotel> selectHotelByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
}
