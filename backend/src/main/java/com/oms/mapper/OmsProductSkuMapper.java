package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.OmsProductSku;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

public interface OmsProductSkuMapper extends BaseMapper<OmsProductSku> {
    /** 商品销量排行 */
    @Select("SELECT s.id, s.sku_code, s.sku_name, s.price, " +
            "COALESCE(SUM(oi.quantity),0) as totalSold, " +
            "COALESCE(SUM(oi.total_price),0) as totalAmount " +
            "FROM oms_product_sku s " +
            "LEFT JOIN oms_order_item oi ON s.id = oi.sku_id " +
            "GROUP BY s.id ORDER BY totalSold DESC LIMIT #{limit}")
    List<Map<String, Object>> selectSalesRanking(int limit);
}
