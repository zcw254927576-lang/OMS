package com.oms.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单查询参数
 */
@Data
public class OrderQueryDTO {
    private String orderNo;
    private String customerName;
    private String customerPhone;
    private String orderStatus;
    private String skuName;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
    private String expressNo;
    private Integer page = 1;
    private Integer pageSize = 20;
}
