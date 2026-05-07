package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.OmsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.util.Map;

public interface OmsOrderMapper extends BaseMapper<OmsOrder> {

    /** 更新订单状态 */
    @Update("UPDATE oms_order SET order_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /** 订单状态分布统计 */
    @Select("SELECT order_status as statusKey, COUNT(*) as count, SUM(payment_amount) as amount " +
            "FROM oms_order WHERE deleted = 0 GROUP BY order_status")
    List<Map<String, Object>> selectOrderStatusStats();

    /** 每日订单统计（近30天） */
    @Select("SELECT DATE(create_time) as dateKey, COUNT(*) as orderCount, " +
            "COALESCE(SUM(payment_amount),0) as amount " +
            "FROM oms_order WHERE deleted = 0 AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY dateKey")
    List<Map<String, Object>> selectDailyOrderStats();

    /** 月度销售额统计 */
    @Select("SELECT DATE_FORMAT(create_time,'%Y-%m') as monthKey, COUNT(*) as orderCount, " +
            "COALESCE(SUM(payment_amount),0) as amount " +
            "FROM oms_order WHERE deleted = 0 AND order_status NOT IN ('CANCELLED') " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL 12 MONTH) " +
            "GROUP BY DATE_FORMAT(create_time,'%Y-%m') ORDER BY monthKey")
    List<Map<String, Object>> selectMonthlySalesStats();

    /** 库存周转统计（各商品出库数量） */
    @Select("SELECT oi.sku_id, oi.sku_code, oi.sku_name, " +
            "SUM(oi.quantity) as outQuantity " +
            "FROM oms_order_item oi JOIN oms_order o ON oi.order_id = o.id " +
            "WHERE o.order_status IN ('DELIVERED','RECEIVED','COMPLETED') " +
            "AND o.delivery_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY oi.sku_id ORDER BY outQuantity DESC")
    List<Map<String, Object>> selectStockTurnoverStats();
}
