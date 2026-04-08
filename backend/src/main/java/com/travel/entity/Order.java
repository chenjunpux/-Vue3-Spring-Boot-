package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Integer orderType;  // 1景点订单 2酒店订单
    
    private Long targetId;
    
    private String targetName;
    
    private BigDecimal totalAmount;
    
    private BigDecimal payAmount;
    
    private Integer status;  // 1待支付 2已支付 3已取消 4已退款
    
    private LocalDateTime payTime;
    
    private String payChannel;
    
    private String contactName;
    
    private String contactPhone;
    
    private Integer quantity;
    
    private LocalDate visitDate;

    // 优惠券相关
    private Long couponId;
    private String couponName;
    private BigDecimal discountAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
