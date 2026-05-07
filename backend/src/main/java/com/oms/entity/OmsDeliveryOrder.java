package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 发货单实体
 */
@Data
@TableName("oms_delivery_order")
public class OmsDeliveryOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deliveryNo;
    private String orderNo;
    private Long orderId;
    private String expressCompany;
    private String expressNo;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Integer totalQuantity;
    private java.math.BigDecimal totalWeight;
    private String status;
    private String operator;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
}
