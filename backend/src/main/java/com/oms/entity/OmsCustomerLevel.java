package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("oms_customer_level")
public class OmsCustomerLevel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String levelName;
    private java.math.BigDecimal minAmount;
    private java.math.BigDecimal maxAmount;
    private java.math.BigDecimal discountRate;
    private String description;
    private LocalDateTime createTime;
}
