package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.UserAddress;
import com.travel.mapper.UserAddressMapper;
import com.travel.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getByUserId(Long userId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreatedAt)
        );
    }

    @Override
    public UserAddress getDefault(Long userId) {
        return baseMapper.selectOne(
            new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
        );
    }

    @Override
    @Transactional
    public void setDefault(Long id, Long userId) {
        // Reset all addresses to non-default
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .set(UserAddress::getIsDefault, 0);
        baseMapper.update(null, wrapper);
        
        // Set the specified address as default
        UserAddress address = baseMapper.selectById(id);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            baseMapper.updateById(address);
        }
    }
}
