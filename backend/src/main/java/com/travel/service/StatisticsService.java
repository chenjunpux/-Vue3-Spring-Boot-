package com.travel.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboardData();
    Map<String, Object> getOrderTrend(String days);
    Map<String, Object> getUserGrowth(String days);
    Map<String, Object> getMonthlyStats(String year);
}
