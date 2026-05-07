package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 实时库存实体
 */
@Data
@TableName("oms_inventory")
public class OmsInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private Integer minStock;
    private Integer maxStock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
