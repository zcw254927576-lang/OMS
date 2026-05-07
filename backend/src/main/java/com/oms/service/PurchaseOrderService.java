package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.dto.PurchaseOrderCreateDTO;
import com.oms.entity.PurchaseOrder;
import com.oms.entity.PurchaseOrderItem;
import java.util.List;

public interface PurchaseOrderService {
    IPage<PurchaseOrder> pageQuery(String keyword, int page, int pageSize);
    PurchaseOrder getById(Long id);
    List<PurchaseOrderItem> getItems(Long orderId);
    void createOrder(PurchaseOrderCreateDTO dto, String createBy);
    void submit(Long id);
    void approve(Long id);
    void warehouse(Long id, String operator);
}
