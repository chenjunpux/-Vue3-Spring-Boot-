package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotels")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String coverImage;
    
    private String city;
    
    private String address;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String description;
    
    private Integer starLevel;
    
    private String facilities;
    
    private Integer hotScore;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
