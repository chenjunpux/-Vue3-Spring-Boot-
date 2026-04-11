package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.User;
import com.travel.mapper.UserMapper;
import com.travel.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public IPage<User> listUsers(Integer page, Integer pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) wrapper.eq(User::getStatus, status);
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void createUser(User user) {
        // 检查用户名唯一性
        LambdaQueryWrapper<User> check = new LambdaQueryWrapper<>();
        check.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(check) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456"); // 默认密码
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getStatus() == null) user.setStatus(1);
        if (user.getRole() == null) user.setRole(1);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(Long id, User user) {
        User existing = userMapper.selectById(id);
        if (existing == null) throw new RuntimeException("用户不存在");
        if (StringUtils.hasText(user.getNickname())) existing.setNickname(user.getNickname());
        if (StringUtils.hasText(user.getPhone())) existing.setPhone(user.getPhone());
        if (StringUtils.hasText(user.getEmail())) existing.setEmail(user.getEmail());
        if (StringUtils.hasText(user.getAvatar())) existing.setAvatar(user.getAvatar());
        if (user.getGender() != null) existing.setGender(user.getGender());
        if (user.getBirthday() != null) existing.setBirthday(user.getBirthday());
        if (user.getRole() != null) existing.setRole(user.getRole());
        if (user.getStatus() != null) existing.setStatus(user.getStatus());
        if (StringUtils.hasText(user.getPassword()) && !user.getPassword().equals(existing.getPassword())) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existing.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        } else {
            throw new RuntimeException("原密码错误");
        }
    }

    @Override
    public void updateAvatar(Long id, String avatar) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setAvatar(avatar);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateProfile(Long id, User userData) {
        User user = userMapper.selectById(id);
        if (user != null) {
            if (StringUtils.hasText(userData.getNickname())) user.setNickname(userData.getNickname());
            if (StringUtils.hasText(userData.getPhone())) user.setPhone(userData.getPhone());
            if (StringUtils.hasText(userData.getEmail())) user.setEmail(userData.getEmail());
            if (userData.getGender() != null) user.setGender(userData.getGender());
            if (userData.getBirthday() != null) user.setBirthday(userData.getBirthday());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateMenuPermissions(Long id, String permissions) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setMenuPermissions(permissions);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
}
