package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.SpotTicket;
import com.travel.service.SpotTicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点门票控制器
 */
@RestController
@RequestMapping("/api/v1/spot-ticket")
public class SpotTicketController {

    private final SpotTicketService spotTicketService;

    public SpotTicketController(SpotTicketService spotTicketService) {
        this.spotTicketService = spotTicketService;
    }

    /**
     * 获取景点门票列表
     */
    @GetMapping("/spot/{spotId}")
    public Result<List<SpotTicket>> getBySpotId(@PathVariable Long spotId) {
        return Result.ok(spotTicketService.getBySpotId(spotId));
    }

    /**
     * 获取门票详情
     */
    @GetMapping("/{id}")
    public Result<SpotTicket> detail(@PathVariable Long id) {
        return Result.ok(spotTicketService.getById(id));
    }
}
