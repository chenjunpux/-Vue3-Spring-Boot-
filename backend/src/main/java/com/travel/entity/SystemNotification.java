package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@Data
@TableName("system_notifications")
public class SystemNotification {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 类型: 1系统通知 2订单通知 3活动通知 */
    private Integer type;

    /** 发送范围: 1全部用户 2指定用户 */
    private Integer targetType;

    /** 指定用户ID列表(JSON) */
    private String targetUserIds;

    /** 状态: 0草稿 1已发布 */
    private Integer status;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
