package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String email;
    
    private Integer gender;  // 0未知 1男 2女
    
    private LocalDateTime birthday;
    
    private Integer role;  // 1普通用户 2管理员
    
    private Integer status;  // 1正常 0封禁

    private String menuPermissions;  // 菜单权限，逗号分隔菜单key

    private LocalDateTime lastLogin;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
