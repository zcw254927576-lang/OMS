package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.dto.DeliveryDTO;
import com.oms.entity.OmsDeliveryOrder;
import java.util.List;

/**
 * 物流发货服务接口
 */
public interface DeliveryService {
    /** 发货单分页 */
    IPage<OmsDeliveryOrder> pageQuery(String orderNo, String status, int page, int pageSize);

    /** 创建发货单并发货 */
    void delivery(DeliveryDTO dto, String operator);

    /** 批量发货 */
    void batchDelivery(List<DeliveryDTO> list, String operator);

    /** 售后审核 */
    void auditAfterSale(Long afterSaleId, boolean approved, String remark, String operator);
}
