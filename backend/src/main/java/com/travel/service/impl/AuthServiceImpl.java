package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.dto.LoginDTO;
import com.travel.dto.RegisterDTO;
import com.travel.dto.AuthResponse;
import com.travel.dto.UserVO;
import com.travel.entity.User;
import com.travel.mapper.UserMapper;
import com.travel.security.JwtUtils;
import com.travel.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse login(LoginDTO dto) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
        );

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被封禁");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse register(RegisterDTO dto) {
        // 检查用户名是否存在
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
        );

        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setRole(1);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.isTokenExpired(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
                    .eq(User::getStatus, 1)
            );

            if (user != null) {
                return buildAuthResponse(user);
            }
        }
        throw new RuntimeException("RefreshToken无效或已过期");
    }

    @Override
    public UserVO getCurrentUser() {
        // 这里通过 SecurityContext 获取，当前实现中由 Controller 层处理
        return null;
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtUtils.generateAccessToken(user.getId(), user.getUsername(), user.getRole()));
        response.setRefreshToken(jwtUtils.generateRefreshToken(user.getUsername()));

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setGender(user.getGender());
        userVO.setRole(user.getRole());
        userVO.setStatus(user.getStatus());
        response.setUser(userVO);

        return response;
    }
}
