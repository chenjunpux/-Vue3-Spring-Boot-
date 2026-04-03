package com.travel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.travel.common.Result;
import com.travel.entity.Spot;
import com.travel.service.SpotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/spot")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping("/list")
    public Result<IPage<Spot>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer status) {
        return Result.ok(spotService.listSpots(page, pageSize, city, keyword, tags, status));
    }

    @GetMapping("/hot")
    public Result<IPage<Spot>> hot(
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(spotService.listHotSpots(page, pageSize, limit));
    }

    @GetMapping("/{id}")
    public Result<Spot> detail(@PathVariable Long id) {
        return Result.ok(spotService.getSpotById(id));
    }

    // 管理端接口
    @PostMapping
    public Result<Void> create(@RequestBody Spot spot) {
        spotService.createSpot(spot);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Spot spot) {
        spot.setId(id);
        spotService.updateSpot(spot);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        spotService.deleteSpot(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        spotService.updateStatus(id, body.get("status"));
        return Result.ok();
    }
}
