package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.User;
import com.travel.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== 管理端用户 CRUD ====================

    @GetMapping("/admin/list")
    public Result<IPage<User>> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.ok(userService.listUsers(page, pageSize, keyword, status));
    }

    @GetMapping("/admin/{id}")
    public Result<User> adminDetail(@PathVariable Long id) {
        return Result.ok(userService.getUserById(id));
    }

    @PostMapping("/admin")
    public Result<Void> create(@RequestBody User user) {
        userService.createUser(user);
        return Result.ok();
    }

    @PutMapping("/admin/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return Result.ok();
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @PutMapping("/admin/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @PutMapping("/admin/{id}/menu-permissions")
    public Result<Void> updateMenuPermissions(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.updateMenuPermissions(id, body.get("permissions"));
        return Result.ok();
    }

    // ==================== 重置密码 ====================
    // 重置为默认密码 123456
    @PutMapping("/admin/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok();
    }

    // ==================== 个人中心 ====================

    @GetMapping("/profile")
    public Result<User> profile(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return Result.ok(user);
        }
        return Result.error(401, "未登录");
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(Authentication authentication, @RequestBody User userData) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userService.updateProfile(user.getId(), userData);
            return Result.ok();
        }
        return Result.error(401, "未登录");
    }

    @PutMapping("/avatar")
    public Result<Void> updateAvatar(Authentication authentication, @RequestBody Map<String, String> body) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userService.updateAvatar(user.getId(), body.get("avatar"));
            return Result.ok();
        }
        return Result.error(401, "未登录");
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(Authentication authentication, @RequestBody Map<String, String> body) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userService.updatePassword(user.getId(), body.get("oldPassword"), body.get("newPassword"));
            return Result.ok();
        }
        return Result.error(401, "未登录");
    }
}
