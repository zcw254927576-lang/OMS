package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oms.mapper.OmsCustomerMapper;
import com.oms.mapper.OmsOrderMapper;
import com.oms.mapper.OmsOrderItemMapper;
import com.oms.mapper.OmsProductSkuMapper;
import com.oms.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OmsOrderMapper orderMapper;
    private final OmsProductSkuMapper skuMapper;
    private final OmsCustomerMapper customerMapper;

    public StatisticsServiceImpl(OmsOrderMapper orderMapper, OmsProductSkuMapper skuMapper,
                                 OmsCustomerMapper customerMapper) {
        this.orderMapper = orderMapper;
        this.skuMapper = skuMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 今日订单数、销售额
        Map<String, Object> todayStats = orderMapper.selectDailyOrderStats().isEmpty()
                ? Map.of("orderCount", 0, "amount", 0)
                : orderMapper.selectDailyOrderStats().stream()
                .reduce((a, b) -> b).orElse(Map.of("orderCount", 0, "amount", 0));

        stats.put("todayOrderCount", todayStats.getOrDefault("orderCount", 0));
        stats.put("todayAmount", todayStats.getOrDefault("amount", 0));

        // 订单状态分布
        stats.put("orderStatusStats", getOrderStatusStats());

        // 近30日趋势
        stats.put("dailyOrderStats", getDailyOrderStats());

        // 商品排行
        stats.put("productSalesRanking", getProductSalesRanking(10));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getOrderStatusStats() {
        return orderMapper.selectOrderStatusStats();
    }

    @Override
    public List<Map<String, Object>> getDailyOrderStats() {
        return orderMapper.selectDailyOrderStats();
    }

    @Override
    public List<Map<String, Object>> getMonthlySalesStats() {
        return orderMapper.selectMonthlySalesStats();
    }

    @Override
    public List<Map<String, Object>> getProductSalesRanking(int limit) {
        return skuMapper.selectSalesRanking(limit);
    }

    @Override
    public List<Map<String, Object>> getStockTurnoverStats() {
        return orderMapper.selectStockTurnoverStats();
    }
}
