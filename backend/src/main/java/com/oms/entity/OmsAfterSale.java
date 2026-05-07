package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 售后单实体
 */
@Data
@TableName("oms_after_sale")
public class OmsAfterSale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String afterSaleNo;
    private String orderNo;
    private Long orderId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String skuSpec;
    private Integer quantity;
    private String afterSaleType;
    private String reason;
    private String status;
    private String applicant;
    private LocalDateTime approveTime;
    private String approveRemark;
    private BigDecimal refundAmount;
    private LocalDateTime refundTime;
    private String returnExpressCompany;
    private String returnExpressNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
