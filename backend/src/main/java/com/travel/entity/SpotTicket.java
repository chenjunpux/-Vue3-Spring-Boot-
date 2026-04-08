package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点门票类型实体
 */
@Data
@TableName("spot_tickets")
public class SpotTicket {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long spotId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Integer validDays;

    private Integer refundable;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
