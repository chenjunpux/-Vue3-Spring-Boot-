package com.travel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.entity.User;

public interface UserService {
    IPage<User> listUsers(Integer page, Integer pageSize, String keyword, Integer status);
    User getUserById(Long id);
    void createUser(User user);
    void updateUser(Long id, User user);
    void deleteUser(Long id);
    void updateStatus(Long id, Integer status);
    void updatePassword(Long id, String oldPassword, String newPassword);
    void updateAvatar(Long id, String avatar);
    void updateProfile(Long id, User user);
    void updateMenuPermissions(Long id, String permissions);
    void resetPassword(Long id);
}
