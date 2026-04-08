package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.RoomInventory;
import com.travel.mapper.RoomInventoryMapper;
import com.travel.service.RoomInventoryService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomInventoryServiceImpl extends ServiceImpl<RoomInventoryMapper, RoomInventory> implements RoomInventoryService {

    @Override
    public List<RoomInventory> getByRoomTypeId(Long roomTypeId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<RoomInventory>()
                .eq(RoomInventory::getRoomTypeId, roomTypeId)
                .ge(RoomInventory::getInventoryDate, startDate)
                .le(RoomInventory::getInventoryDate, endDate)
                .orderByAsc(RoomInventory::getInventoryDate)
        );
    }

    @Override
    public void updateInventory(Long roomTypeId, LocalDate date, Integer availableRooms) {
        RoomInventory inv = baseMapper.selectOne(
            new LambdaQueryWrapper<RoomInventory>()
                .eq(RoomInventory::getRoomTypeId, roomTypeId)
                .eq(RoomInventory::getInventoryDate, date)
        );
        if (inv != null) {
            inv.setAvailableRooms(availableRooms);
            baseMapper.updateById(inv);
        } else {
            inv = new RoomInventory();
            inv.setRoomTypeId(roomTypeId);
            inv.setInventoryDate(date);
            inv.setAvailableRooms(availableRooms);
            baseMapper.insert(inv);
        }
    }

    @Override
    public boolean checkAvailability(Long roomTypeId, LocalDate date, Integer quantity) {
        RoomInventory inv = baseMapper.selectOne(
            new LambdaQueryWrapper<RoomInventory>()
                .eq(RoomInventory::getRoomTypeId, roomTypeId)
                .eq(RoomInventory::getInventoryDate, date)
        );
        if (inv == null) return true; // 无库存记录，默认有房
        return inv.getAvailableRooms() >= quantity;
    }
}
