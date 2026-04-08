package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.RoomInventory;
import com.travel.service.RoomInventoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 房间库存控制器
 */
@RestController
@RequestMapping("/api/v1/room-inventory")
public class RoomInventoryController {

    private final RoomInventoryService roomInventoryService;

    public RoomInventoryController(RoomInventoryService roomInventoryService) {
        this.roomInventoryService = roomInventoryService;
    }

    /**
     * 获取房型库存日历
     */
    @GetMapping("/room-type/{roomTypeId}")
    public Result<List<RoomInventory>> getByRoomTypeId(
            @PathVariable Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(roomInventoryService.getByRoomTypeId(roomTypeId, startDate, endDate));
    }

    /**
     * 检查库存是否充足
     */
    @GetMapping("/check")
    public Result<Boolean> checkAvailability(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer quantity) {
        return Result.ok(roomInventoryService.checkAvailability(roomTypeId, date, quantity));
    }

    /**
     * 更新库存（管理端）
     */
    @PutMapping
    public Result<Void> updateInventory(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer availableRooms) {
        roomInventoryService.updateInventory(roomTypeId, date, availableRooms);
        return Result.ok();
    }
}
