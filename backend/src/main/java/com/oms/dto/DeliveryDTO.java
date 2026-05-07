package com.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 发货请求参数
 */
@Data
public class DeliveryDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    @NotBlank(message = "快递公司不能为空")
    private String expressCompany;
    @NotBlank(message = "快递单号不能为空")
    private String expressNo;
    private List<DeliveryItemDTO> items;

    @Data
    public static class DeliveryItemDTO {
        private Long skuId;
        private Integer quantity;
    }
}
