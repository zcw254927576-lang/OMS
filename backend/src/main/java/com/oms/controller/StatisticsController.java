package com.oms.controller;

import com.oms.common.R;
import com.oms.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 统计报表控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /** 仪表盘概览 */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        return R.ok(statisticsService.getDashboardStats());
    }

    /** 订单状态分布 */
    @GetMapping("/order-status")
    public R<List<Map<String, Object>>> orderStatus() {
        return R.ok(statisticsService.getOrderStatusStats());
    }

    /** 每日订单统计 */
    @GetMapping("/daily-order")
    public R<List<Map<String, Object>>> dailyOrder() {
        return R.ok(statisticsService.getDailyOrderStats());
    }

    /** 月度销售额 */
    @GetMapping("/monthly-sales")
    public R<List<Map<String, Object>>> monthlySales() {
        return R.ok(statisticsService.getMonthlySalesStats());
    }

    /** 商品销量排行 */
    @GetMapping("/product-ranking")
    public R<List<Map<String, Object>>> productRanking(
            @RequestParam(defaultValue = "20") int limit) {
        return R.ok(statisticsService.getProductSalesRanking(limit));
    }

    /** 库存周转率 */
    @GetMapping("/stock-turnover")
    public R<List<Map<String, Object>>> stockTurnover() {
        return R.ok(statisticsService.getStockTurnoverStats());
    }
}
