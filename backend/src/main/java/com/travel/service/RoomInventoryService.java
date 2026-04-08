package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.RoomInventory;
import java.time.LocalDate;
import java.util.List;

public interface RoomInventoryService extends IService<RoomInventory> {
    List<RoomInventory> getByRoomTypeId(Long roomTypeId, LocalDate startDate, LocalDate endDate);
    void updateInventory(Long roomTypeId, LocalDate date, Integer availableRooms);
    boolean checkAvailability(Long roomTypeId, LocalDate date, Integer quantity);
}
