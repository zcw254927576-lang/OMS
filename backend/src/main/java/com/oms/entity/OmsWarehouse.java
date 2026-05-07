package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 仓库实体
 */
@Data
@TableName("oms_warehouse")
public class OmsWarehouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseName;
    private String warehouseCode;
    private String contactPerson;
    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer status;
    private LocalDateTime createTime;
}
