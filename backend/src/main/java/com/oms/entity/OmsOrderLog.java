package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单操作日志实体
 */
@Data
@TableName("oms_order_log")
public class OmsOrderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String orderNo;
    private String operationType;
    private String beforeStatus;
    private String afterStatus;
    private String operator;
    private String operationContent;
    private LocalDateTime createTime;
}
