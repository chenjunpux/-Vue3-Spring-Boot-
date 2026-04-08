package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件实体
 */
@Data
@TableName("files")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private Long fileSize;

    private String fileType;

    private String fileExt;

    private String storageType;

    private Long userId;

    private String bucketName;

    private String bizType;

    private Long bizId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
