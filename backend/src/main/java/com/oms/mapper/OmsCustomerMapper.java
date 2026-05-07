package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.OmsCustomer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OmsCustomerMapper extends BaseMapper<OmsCustomer> {
    /** 客户消费统计 */
    @Select("SELECT COUNT(*) as orderCount, COALESCE(SUM(payment_amount),0) as totalAmount " +
            "FROM oms_order WHERE customer_id = #{customerId} AND deleted = 0")
    Map<String, Object> selectCustomerStats(Long customerId);

    /** 更新客户消费统计 */
    @Update("UPDATE oms_customer SET total_amount = total_amount + #{amount}, " +
            "order_count = order_count + 1, last_order_time = NOW() WHERE id = #{customerId}")
    void updateCustomerOrderStats(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

    /** 客户复购率统计 */
    @Select("SELECT COUNT(*) as customerCount, " +
            "SUM(CASE WHEN order_count >= 2 THEN 1 ELSE 0 END) as repurchaseCount " +
            "FROM oms_customer WHERE order_count > 0")
    Map<String, Object> selectRepurchaseStats();

    /** 客户排行查询 */
    List<Map<String, Object>> selectCustomerRanking(@Param("limit") int limit);
}
