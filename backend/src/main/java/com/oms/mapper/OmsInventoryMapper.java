package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.OmsInventory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

public interface OmsInventoryMapper extends BaseMapper<OmsInventory> {
    /** 库存预警列表（可用库存低于最低库存） */
    @Select("SELECT * FROM oms_inventory WHERE available_quantity < min_stock AND min_stock > 0")
    List<OmsInventory> selectLowStockList();

    /** 锁定库存（下单时调用） */
    @Update("UPDATE oms_inventory SET locked_quantity = locked_quantity + #{quantity}, " +
            "available_quantity = available_quantity - #{quantity} " +
            "WHERE sku_id = #{skuId} AND available_quantity >= #{quantity}")
    int lockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /** 释放锁定库存（取消订单时调用） */
    @Update("UPDATE oms_inventory SET locked_quantity = locked_quantity - #{quantity}, " +
            "available_quantity = available_quantity + #{quantity} " +
            "WHERE sku_id = #{skuId} AND locked_quantity >= #{quantity}")
    int unlockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /** 扣减实际库存（发货时调用） */
    @Update("UPDATE oms_inventory SET quantity = quantity - #{quantity}, " +
            "locked_quantity = locked_quantity - #{quantity} " +
            "WHERE sku_id = #{skuId} AND locked_quantity >= #{quantity}")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /** 回冲库存（退货时调用） */
    @Update("UPDATE oms_inventory SET quantity = quantity + #{quantity}, " +
            "available_quantity = available_quantity + #{quantity} " +
            "WHERE sku_id = #{skuId}")
    int returnStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
