package com.oms.common;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING_AUDIT("待审核"),
    PAID("已付款"),
    PENDING_DELIVERY("待发货"),
    DELIVERED("已发货"),
    RECEIVED("已签收"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    AFTER_SALE("售后中");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
