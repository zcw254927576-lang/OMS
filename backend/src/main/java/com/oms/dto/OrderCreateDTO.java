package com.oms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建请求
 */
@Data
public class OrderCreateDTO {
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // 收货信息
    @NotBlank(message = "收货人不能为空")
    private String receiverName;
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    private String buyerMessage;
    private BigDecimal shippingAmount;

    // 订单商品
    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品SKU_ID不能为空")
        private Long skuId;
        private String skuCode;
        private String skuName;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
    }
}
