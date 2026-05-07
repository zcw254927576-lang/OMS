package com.oms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderCreateDTO {
    private String supplierName;
    private String remark;

    @NotEmpty(message = "采购商品不能为空")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        private Long skuId;
        private String skuName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
