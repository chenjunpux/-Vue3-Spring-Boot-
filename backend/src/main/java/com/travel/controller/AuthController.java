package com.travel.controller;

import com.travel.common.Result;
import com.travel.dto.LoginDTO;
import com.travel.dto.RegisterDTO;
import com.travel.dto.AuthResponse;
import com.travel.dto.UserVO;
import com.travel.entity.User;
import com.travel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody @Valid LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody @Valid RegisterDTO dto) {
        return Result.ok(authService.register(dto));
    }

    @PostMapping("/refresh")
    public Result<AuthResponse> refresh(@RequestBody String refreshToken) {
        return Result.ok(authService.refreshToken(refreshToken.replace("\"", "")));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setGender(user.getGender());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            return Result.ok(vo);
        }
        return Result.error(401, "未登录");
    }
}
