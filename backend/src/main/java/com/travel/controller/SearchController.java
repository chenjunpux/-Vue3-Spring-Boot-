package com.travel.controller;

import com.travel.common.Result;
import com.travel.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 全局搜索控制器
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 全局搜索
     */
    @GetMapping
    public Result<Map<String, Object>> globalSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = searchService.globalSearch(keyword, page, pageSize);
        return Result.ok(result);
    }

    /**
     * 搜索景点
     */
    @GetMapping("/spots")
    public Result<Object> searchSpots(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(searchService.searchSpots(keyword, limit));
    }

    /**
     * 搜索酒店
     */
    @GetMapping("/hotels")
    public Result<Object> searchHotels(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(searchService.searchHotels(keyword, limit));
    }

    /**
     * 搜索游记
     */
    @GetMapping("/articles")
    public Result<Object> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(searchService.searchArticles(keyword, limit));
    }
}
