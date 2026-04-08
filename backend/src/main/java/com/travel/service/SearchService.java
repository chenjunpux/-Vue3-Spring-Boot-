package com.travel.service;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Map<String, Object> globalSearch(String keyword, Integer page, Integer pageSize);
    List<Map<String, Object>> searchSpots(String keyword, Integer limit);
    List<Map<String, Object>> searchHotels(String keyword, Integer limit);
    List<Map<String, Object>> searchArticles(String keyword, Integer limit);
}
