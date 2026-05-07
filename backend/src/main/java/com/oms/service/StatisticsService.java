package com.oms.service;

import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口
 */
public interface StatisticsService {
    /** 仪表盘概览数据 */
    Map<String, Object> getDashboardStats();

    /** 订单状态分布 */
    List<Map<String, Object>> getOrderStatusStats();

    /** 近30日每日订单统计 */
    List<Map<String, Object>> getDailyOrderStats();

    /** 月度销售额统计 */
    List<Map<String, Object>> getMonthlySalesStats();

    /** 商品销量排行 */
    List<Map<String, Object>> getProductSalesRanking(int limit);

    /** 库存周转率 */
    List<Map<String, Object>> getStockTurnoverStats();
}
