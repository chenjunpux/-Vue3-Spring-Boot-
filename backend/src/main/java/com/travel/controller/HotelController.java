package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.Hotel;
import com.travel.entity.RoomType;
import com.travel.service.HotelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/list")
    public Result<IPage<Hotel>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.ok(hotelService.listHotels(page, pageSize, city, keyword, status));
    }

    @GetMapping("/hot")
    public Result<IPage<Hotel>> hot(
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(hotelService.listHotHotels(page, pageSize, limit));
    }

    @GetMapping("/{id}")
    public Result<Hotel> detail(@PathVariable Long id) {
        return Result.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/{id}/rooms")
    public Result<List<RoomType>> rooms(@PathVariable Long id) {
        return Result.ok(hotelService.getRoomTypesByHotelId(id));
    }

    // 管理端接口
    @PostMapping
    public Result<Void> create(@RequestBody Hotel hotel) {
        hotelService.createHotel(hotel);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Hotel hotel) {
        hotel.setId(id);
        hotelService.updateHotel(hotel);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        hotelService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @PostMapping("/rooms")
    public Result<Void> createRoom(@RequestBody RoomType roomType) {
        hotelService.createRoomType(roomType);
        return Result.ok();
    }

    @PutMapping("/rooms/{id}")
    public Result<Void> updateRoom(@PathVariable Long id, @RequestBody RoomType roomType) {
        roomType.setId(id);
        hotelService.updateRoomType(roomType);
        return Result.ok();
    }

    @DeleteMapping("/rooms/{id}")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        hotelService.deleteRoomType(id);
        return Result.ok();
    }
}
