package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.dto.OrderCreateDTO;
import com.oms.dto.OrderQueryDTO;
import com.oms.entity.OmsOrder;
import com.oms.entity.OmsOrderItem;
import com.oms.entity.OmsOrderLog;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {
    /** 分页查询订单 */
    IPage<OmsOrder> pageQuery(OrderQueryDTO dto);

    /** 获取订单详情 */
    OmsOrder getById(Long id);

    /** 获取订单明细 */
    List<OmsOrderItem> getItems(Long orderId);

    /** 获取订单操作日志 */
    List<OmsOrderLog> getLogs(Long orderId);

    /** 创建订单 */
    OmsOrder createOrder(OrderCreateDTO dto, String operator);

    /** 更新订单状态 */
    void updateStatus(Long id, String status, String operator, String content);

    /** 取消订单 */
    void cancelOrder(Long id, String reason, String operator);

    /** 删除订单 */
    void deleteOrder(Long id);

    /** 拆单 */
    List<OmsOrder> splitOrder(Long orderId, List<Map<Long, Integer>> splitGroups, String operator);

    /** 合单 */
    OmsOrder mergeOrders(List<Long> orderIds, String operator);

    /** 导出订单Excel */
    void exportExcel(OrderQueryDTO dto, HttpServletResponse response);
}
