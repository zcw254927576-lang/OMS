package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 商品SKU实体
 */
@Data
@TableName("oms_product_sku")
public class OmsProductSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String skuCode;
    private String skuName;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String specs;
    private String unit;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
