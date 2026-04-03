package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.*;
import com.travel.mapper.*;
import com.travel.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final SpotMapper spotMapper;
    private final HotelMapper hotelMapper;
    private final ArticleMapper articleMapper;

    public StatisticsServiceImpl(UserMapper userMapper, OrderMapper orderMapper, 
                                  SpotMapper spotMapper, HotelMapper hotelMapper, ArticleMapper articleMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.spotMapper = spotMapper;
        this.hotelMapper = hotelMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 总用户数
        long totalUsers = userMapper.selectCount(null);
        data.put("totalUsers", totalUsers);

        // 今日订单数
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayOrders = orderMapper.selectCount(
            new LambdaQueryWrapper<Order>().ge(Order::getCreatedAt, today)
        );
        data.put("todayOrders", todayOrders);

        // 今日收入
        List<Order> paidOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreatedAt, today)
                .eq(Order::getStatus, 2)
        );
        BigDecimal todayIncome = paidOrders.stream()
            .map(Order::getPayAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("todayIncome", todayIncome);

        // 景点总数
        long totalSpots = spotMapper.selectCount(new LambdaQueryWrapper<Spot>().eq(Spot::getStatus, 1));
        data.put("totalSpots", totalSpots);

        // 酒店总数
        long totalHotels = hotelMapper.selectCount(new LambdaQueryWrapper<Hotel>().eq(Hotel::getStatus, 1));
        data.put("totalHotels", totalHotels);

        // 游记总数
        long totalArticles = articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));
        data.put("totalArticles", totalArticles);

        // 待处理订单数
        long pendingOrders = orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1));
        data.put("pendingOrders", pendingOrders);

        return data;
    }

    @Override
    public Map<String, Object> getOrderTrend(String days) {
        Map<String, Object> data = new HashMap<>();
        int dayCount = 30;
        if (days != null) {
            try { dayCount = Integer.parseInt(days); } catch (Exception ignored) {}
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(dayCount);
        List<Order> orders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreatedAt, startDate)
                .eq(Order::getStatus, 2)
        );

        // 按天分组
        Map<String, List<Order>> byDay = orders.stream()
            .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate().toString()));

        List<String> dates = new ArrayList<>();
        List<Integer> spotOrders = new ArrayList<>();
        List<Integer> hotelOrders = new ArrayList<>();

        for (int i = dayCount - 1; i >= 0; i--) {
            LocalDateTime d = LocalDateTime.now().minusDays(i);
            String dateStr = d.toLocalDate().toString();
            dates.add(dateStr.substring(5));  // MM-DD

            List<Order> dayOrders = byDay.getOrDefault(dateStr, Collections.emptyList());
            spotOrders.add((int) dayOrders.stream().filter(o -> o.getOrderType() == 1).count());
            hotelOrders.add((int) dayOrders.stream().filter(o -> o.getOrderType() == 2).count());
        }

        data.put("dates", dates);
        data.put("spotOrders", spotOrders);
        data.put("hotelOrders", hotelOrders);
        return data;
    }

    @Override
    public Map<String, Object> getUserGrowth(String days) {
        Map<String, Object> data = new HashMap<>();
        int dayCount = 30;
        if (days != null) {
            try { dayCount = Integer.parseInt(days); } catch (Exception ignored) {}
        }

        LocalDateTime startDate = LocalDateTime.now().minusDays(dayCount);
        List<User> users = userMapper.selectList(
            new LambdaQueryWrapper<User>().ge(User::getCreatedAt, startDate)
        );

        Map<String, Long> byDay = users.stream()
            .collect(Collectors.groupingBy(
                u -> u.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        long cumulative = userMapper.selectCount(null) - users.size();

        for (int i = dayCount - 1; i >= 0; i--) {
            LocalDateTime d = LocalDateTime.now().minusDays(i);
            String dateStr = d.toLocalDate().toString();
            dates.add(dateStr.substring(5));
            cumulative += byDay.getOrDefault(dateStr, 0L);
            counts.add(cumulative);
        }

        data.put("dates", dates);
        data.put("counts", counts);
        return data;
    }

    @Override
    public Map<String, Object> getMonthlyStats(String yearStr) {
        Map<String, Object> data = new HashMap<>();
        int year = LocalDateTime.now().getYear();
        if (yearStr != null) {
            try { year = Integer.parseInt(yearStr); } catch (Exception ignored) {}
        }

        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<Order> orders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .ge(Order::getCreatedAt, startOfYear)
                .le(Order::getCreatedAt, endOfYear)
        );

        // 按月分组
        Map<String, List<Order>> byMonth = orders.stream()
            .collect(Collectors.groupingBy(o -> String.valueOf(o.getCreatedAt().getMonthValue())));

        // 按支付状态分组统计
        Map<String, List<Order>> byMonthPaid = orders.stream()
            .filter(o -> o.getStatus() == 2)  // 已支付
            .collect(Collectors.groupingBy(o -> String.valueOf(o.getCreatedAt().getMonthValue())));

        String[] months = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};

        List<Integer> spotBuyCounts = new ArrayList<>();
        List<Integer> hotelBuyCounts = new ArrayList<>();
        List<Integer> spotSellCounts = new ArrayList<>();
        List<Integer> hotelSellCounts = new ArrayList<>();
        List<BigDecimal> spotIncomes = new ArrayList<>();
        List<BigDecimal> hotelIncomes = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            String key = String.valueOf(m);
            List<Order> monthOrders = byMonth.getOrDefault(key, Collections.emptyList());
            List<Order> monthPaid = byMonthPaid.getOrDefault(key, Collections.emptyList());

            // 景点购买（用户买点门票）
            spotBuyCounts.add((int) monthOrders.stream().filter(o -> o.getOrderType() == 1).count());
            // 酒店购买（用户订酒店）
            hotelBuyCounts.add((int) monthOrders.stream().filter(o -> o.getOrderType() == 2).count());
            // 已支付才算卖出的收入
            spotSellCounts.add((int) monthPaid.stream().filter(o -> o.getOrderType() == 1).count());
            hotelSellCounts.add((int) monthPaid.stream().filter(o -> o.getOrderType() == 2).count());
            // 收入
            BigDecimal spotInc = monthPaid.stream().filter(o -> o.getOrderType() == 1)
                .map(Order::getPayAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal hotelInc = monthPaid.stream().filter(o -> o.getOrderType() == 2)
                .map(Order::getPayAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            spotIncomes.add(spotInc);
            hotelIncomes.add(hotelInc);
        }

        data.put("year", year);
        data.put("months", months);
        data.put("spotBuyCounts", spotBuyCounts);
        data.put("hotelBuyCounts", hotelBuyCounts);
        data.put("spotSellCounts", spotSellCounts);
        data.put("hotelSellCounts", hotelSellCounts);
        data.put("spotIncomes", spotIncomes);
        data.put("hotelIncomes", hotelIncomes);
        return data;
    }
}
