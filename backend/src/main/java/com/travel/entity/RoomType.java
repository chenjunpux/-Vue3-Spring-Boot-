package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room_types")
public class RoomType {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hotelId;
    
    private String name;
    
    private BigDecimal price;
    
    private String bedType;
    
    private Integer maxGuest;
    
    private Integer totalRooms;
    
    private String amenities;
    
    private String images;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
