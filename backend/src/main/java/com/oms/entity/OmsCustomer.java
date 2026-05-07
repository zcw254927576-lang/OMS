package com.oms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 客户实体
 */
@Data
@TableName("oms_customer")
public class OmsCustomer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerNo;
    private String customerName;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthDate;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Long levelId;
    private String levelName;
    private Long totalPoints;
    private Long availablePoints;
    private java.math.BigDecimal totalAmount;
    private Integer orderCount;
    private LocalDateTime lastOrderTime;
    private String tag;
    private String source;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
