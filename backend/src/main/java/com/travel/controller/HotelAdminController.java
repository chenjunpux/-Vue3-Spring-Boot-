package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Hotel;
import com.travel.service.HotelService;
import com.travel.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/hotel/admin")
public class HotelAdminController {

    private final HotelService hotelService;

    public HotelAdminController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /**
     * 管理端酒店列表（分页 + 筛选）
     */
    @GetMapping("/list")
    public Result<IPage<Hotel>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.ok(hotelService.listHotels(page, pageSize, city, keyword, status));
    }

    /**
     * 创建酒店
     */
    @PostMapping
    public Result<Void> create(@RequestBody Hotel hotel) {
        hotelService.createHotel(hotel);
        return Result.ok(null);
    }

    /**
     * 更新酒店
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Hotel hotel) {
        hotel.setId(id);
        hotelService.updateHotel(hotel);
        return Result.ok(null);
    }

    /**
     * 删除酒店
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return Result.ok(null);
    }

    /**
     * 更新酒店状态（上架/下架）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        hotelService.updateStatus(id, body.get("status"));
        return Result.ok(null);
    }
}
