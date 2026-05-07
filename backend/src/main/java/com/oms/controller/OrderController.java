package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.dto.OrderCreateDTO;
import com.oms.dto.OrderQueryDTO;
import com.oms.entity.OmsOrder;
import com.oms.entity.OmsOrderItem;
import com.oms.entity.OmsOrderLog;
import com.oms.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 分页查询订单 */
    @PostMapping("/page")
    public R<IPage<OmsOrder>> page(@RequestBody OrderQueryDTO dto) {
        return R.ok(orderService.pageQuery(dto));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public R<OmsOrder> detail(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    /** 订单明细 */
    @GetMapping("/{id}/items")
    public R<List<OmsOrderItem>> items(@PathVariable Long id) {
        return R.ok(orderService.getItems(id));
    }

    /** 订单操作日志 */
    @GetMapping("/{id}/logs")
    public R<List<OmsOrderLog>> logs(@PathVariable Long id) {
        return R.ok(orderService.getLogs(id));
    }

    /** 创建订单 */
    @PostMapping
    public R<OmsOrder> create(@Valid @RequestBody OrderCreateDTO dto, Authentication auth) {
        String operator = (String) auth.getCredentials();
        return R.ok(orderService.createOrder(dto, operator));
    }

    /** 更新订单状态 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        orderService.updateStatus(id, body.get("status"), (String) auth.getCredentials(), body.get("content"));
        return R.ok();
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        orderService.cancelOrder(id, body.get("reason"), (String) auth.getCredentials());
        return R.ok();
    }

    /** 删除订单 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return R.ok();
    }

    /** 拆单 */
    @PostMapping("/{id}/split")
    public R<List<OmsOrder>> split(@PathVariable Long id, @RequestBody List<Map<Long, Integer>> groups, Authentication auth) {
        return R.ok(orderService.splitOrder(id, groups, (String) auth.getCredentials()));
    }

    /** 合单 */
    @PostMapping("/merge")
    public R<OmsOrder> merge(@RequestBody List<Long> orderIds, Authentication auth) {
        return R.ok(orderService.mergeOrders(orderIds, (String) auth.getCredentials()));
    }

    /** 导出Excel */
    @PostMapping("/export")
    public void export(@RequestBody OrderQueryDTO dto, HttpServletResponse response) {
        orderService.exportExcel(dto, response);
    }
}
