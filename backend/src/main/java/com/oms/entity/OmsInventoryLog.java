package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存变动日志实体
 */
@Data
@TableName("oms_inventory_log")
public class OmsInventoryLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private String skuCode;
    private String changeType;
    private Integer quantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String relatedNo;
    private String operator;
    private String remark;
    private LocalDateTime createTime;
}
