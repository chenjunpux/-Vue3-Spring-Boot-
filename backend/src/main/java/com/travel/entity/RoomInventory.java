package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 房间库存实体
 */
@Data
@TableName("room_inventory")
public class RoomInventory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomTypeId;

    private LocalDate inventoryDate;

    private Integer availableRooms;

    private BigDecimal price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
