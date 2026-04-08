package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 景点开放时间实体
 */
@Data
@TableName("spot_schedules")
public class SpotSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long spotId;

    private Integer dayOfWeek;

    private String startTime;

    private String endTime;

    private Integer isClosed;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
