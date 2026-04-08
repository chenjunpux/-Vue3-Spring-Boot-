package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.UserAddress;
import java.util.List;

public interface UserAddressService extends IService<UserAddress> {
    List<UserAddress> getByUserId(Long userId);
    UserAddress getDefault(Long userId);
    void setDefault(Long id, Long userId);
}
