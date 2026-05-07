package com.oms.dto;

import lombok.Data;

/**
 * 审核请求参数
 */
@Data
public class AuditDTO {
    private boolean approved;
    private String remark;
}
