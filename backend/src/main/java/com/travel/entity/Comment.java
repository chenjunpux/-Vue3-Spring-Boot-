package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Integer targetType;  // 1景点 2酒店 3游记 4订单
    
    private Long targetId;
    
    private Long parentId;
    
    private String content;
    
    private Integer rating;
    
    private Integer likeCount;
    
    private Integer status;  // 1正常 0删除
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
