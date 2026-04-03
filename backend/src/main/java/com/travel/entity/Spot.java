package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("spots")
public class Spot {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String coverImage;
    
    private String city;
    
    private String address;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String description;
    
    private BigDecimal ticketPrice;
    
    private String openTime;
    
    private Integer suggestedTime;
    
    private String level;
    
    private String tags;
    
    private Integer hotScore;
    
    private Integer viewCount;
    
    private Integer status;
    
    // 详情页新增字段
    private BigDecimal score;
    
    private String traffic;
    
    private String tips;
    
    private String section1Title;
    
    private String section1Content;
    
    private String section1Image;
    
    private String section2Title;
    
    private String section2Content;
    
    private String section2Image;
    
    private String section3Title;
    
    private String section3Content;
    
    private String section3Image;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
