package com.travel.controller;

import com.travel.common.Result;
import com.travel.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.ok(statisticsService.getDashboardData());
    }

    @GetMapping("/orderTrend")
    public Result<Map<String, Object>> orderTrend(@RequestParam(required = false) String days) {
        return Result.ok(statisticsService.getOrderTrend(days));
    }

    @GetMapping("/userGrowth")
    public Result<Map<String, Object>> userGrowth(@RequestParam(required = false) String days) {
        return Result.ok(statisticsService.getUserGrowth(days));
    }

    @GetMapping("/monthly")
    public Result<Map<String, Object>> monthlyStats(@RequestParam(required = false) String year) {
        return Result.ok(statisticsService.getMonthlyStats(year));
    }
}
