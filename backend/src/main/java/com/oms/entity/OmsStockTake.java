package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存盘点实体
 */
@Data
@TableName("oms_stock_take")
public class OmsStockTake {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String takeNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private String operator;
    private Integer totalSku;
    private Integer matchedSku;
    private Integer profitSku;
    private Integer lossSku;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
    private String remark;
}
