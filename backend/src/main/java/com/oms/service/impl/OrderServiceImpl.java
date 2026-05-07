package com.oms.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oms.dto.OrderCreateDTO;
import com.oms.dto.OrderQueryDTO;
import com.oms.entity.*;
import com.oms.exception.BusinessException;
import com.oms.mapper.*;
import com.oms.service.InventoryService;
import com.oms.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OrderService {

    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsOrderLogMapper orderLogMapper;
    private final OmsProductSkuMapper skuMapper;
    private final OmsCustomerMapper customerMapper;
    private final OmsCustomerMapper omsCustomerMapper;
    private final InventoryService inventoryService;

    private static final AtomicLong ORDER_SEQ = new AtomicLong(1);

    public OrderServiceImpl(OmsOrderMapper orderMapper, OmsOrderItemMapper orderItemMapper,
                            OmsOrderLogMapper orderLogMapper, OmsProductSkuMapper skuMapper,
                            OmsCustomerMapper customerMapper, OmsCustomerMapper omsCustomerMapper,
                            InventoryService inventoryService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderLogMapper = orderLogMapper;
        this.skuMapper = skuMapper;
        this.customerMapper = customerMapper;
        this.omsCustomerMapper = omsCustomerMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    public IPage<OmsOrder> pageQuery(OrderQueryDTO dto) {
        Page<OmsOrder> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<OmsOrder>()
                .eq(StringUtils.hasText(dto.getOrderNo()), OmsOrder::getOrderNo, dto.getOrderNo())
                .like(StringUtils.hasText(dto.getCustomerName()), OmsOrder::getCustomerName, dto.getCustomerName())
                .eq(StringUtils.hasText(dto.getCustomerPhone()), OmsOrder::getCustomerPhone, dto.getCustomerPhone())
                .eq(StringUtils.hasText(dto.getOrderStatus()), OmsOrder::getOrderStatus, dto.getOrderStatus())
                .ge(dto.getCreateTimeStart() != null, OmsOrder::getCreateTime, dto.getCreateTimeStart())
                .le(dto.getCreateTimeEnd() != null, OmsOrder::getCreateTime, dto.getCreateTimeEnd())
                .orderByDesc(OmsOrder::getCreateTime);
        return orderMapper.selectPage(page, wrapper);
    }

    @Override
    public OmsOrder getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<OmsOrderItem> getItems(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OmsOrderLog> getLogs(Long orderId) {
        return orderLogMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrder createOrder(OrderCreateDTO dto, String operator) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();

        // 2. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        BigDecimal shippingAmount = dto.getShippingAmount() != null ? dto.getShippingAmount() : BigDecimal.ZERO;
        BigDecimal paymentAmount = totalAmount.add(shippingAmount);

        // 3. 保存订单
        OmsOrder order = new OmsOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(dto.getCustomerId());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setShippingAmount(shippingAmount);
        order.setPaymentAmount(paymentAmount);
        order.setOrderStatus("PENDING_AUDIT");
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setProvince(dto.getProvince());
        order.setCity(dto.getCity());
        order.setDistrict(dto.getDistrict());
        order.setDetailAddress(dto.getDetailAddress());
        order.setBuyerMessage(dto.getBuyerMessage());
        order.setCreateBy(operator);
        orderMapper.insert(order);

        // 4. 保存订单明细
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            OmsProductSku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) {
                throw new BusinessException("商品不存在：" + item.getSkuId());
            }
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setSkuCode(sku.getSkuCode());
            orderItem.setSkuName(sku.getSkuName());
            orderItem.setSkuSpec(sku.getSpecs());
            orderItem.setCategoryName(sku.getCategoryName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemMapper.insert(orderItem);

            // 锁定库存
            inventoryService.lockStock(item.getSkuId(), item.getQuantity(), orderNo, operator);
        }

        // 5. 记录操作日志
        addOrderLog(order.getId(), orderNo, "CREATE", null, "PENDING_AUDIT", operator, "创建订单");

        // 6. 更新客户统计
        try {
            omsCustomerMapper.updateCustomerOrderStats(dto.getCustomerId(), paymentAmount);
        } catch (Exception ignored) {}

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status, String operator, String content) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("订单不存在");
        String before = order.getOrderStatus();
        order.setOrderStatus(status);
        orderMapper.updateById(order);
        addOrderLog(id, order.getOrderNo(), "UPDATE_STATUS", before, status, operator, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id, String reason, String operator) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("订单不存在");
        String before = order.getOrderStatus();
        order.setOrderStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 释放库存
        List<OmsOrderItem> items = orderItemMapper.selectByOrderId(id);
        for (OmsOrderItem item : items) {
            inventoryService.unlockStock(item.getSkuId(), item.getQuantity(), order.getOrderNo(), operator);
        }
        addOrderLog(id, order.getOrderNo(), "CANCEL", before, "CANCELLED", operator, "取消订单：" + reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        OmsOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("订单不存在");
        orderMapper.deleteById(id); // 逻辑删除
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OmsOrder> splitOrder(Long orderId, List<Map<Long, Integer>> splitGroups, String operator) {
        OmsOrder sourceOrder = orderMapper.selectById(orderId);
        if (sourceOrder == null) throw new BusinessException("订单不存在");
        if (!"PENDING_AUDIT".equals(sourceOrder.getOrderStatus())) {
            throw new BusinessException("只有待审核订单可以拆单");
        }

        List<OmsOrderItem> sourceItems = orderItemMapper.selectByOrderId(orderId);
        List<OmsOrder> newOrders = new ArrayList<>();

        for (Map<Long, Integer> group : splitGroups) {
            OmsOrder newOrder = cloneOrder(sourceOrder, operator);
            BigDecimal groupTotal = BigDecimal.ZERO;
            for (Map.Entry<Long, Integer> entry : group.entrySet()) {
                Long skuId = entry.getKey();
                Integer qty = entry.getValue();
                OmsOrderItem sourceItem = sourceItems.stream()
                        .filter(i -> i.getSkuId().equals(skuId)).findFirst().orElse(null);
                if (sourceItem == null) continue;
                if (qty > sourceItem.getQuantity()) {
                    throw new BusinessException("拆单数量超过原明细数量");
                }

                OmsOrderItem newItem = cloneItem(newOrder.getId(), sourceItem, qty);
                orderItemMapper.insert(newItem);
                groupTotal = groupTotal.add(newItem.getTotalPrice());
            }
            newOrder.setTotalAmount(groupTotal);
            newOrder.setPaymentAmount(groupTotal.add(newOrder.getShippingAmount()));
            orderMapper.updateById(newOrder);
            newOrders.add(newOrder);
            addOrderLog(newOrder.getId(), newOrder.getOrderNo(), "SPLIT", null, "PENDING_AUDIT", operator, "由订单" + sourceOrder.getOrderNo() + "拆分");
        }

        // 原订单取消
        sourceOrder.setOrderStatus("CANCELLED");
        sourceOrder.setCancelReason("拆单");
        sourceOrder.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(sourceOrder);
        addOrderLog(sourceOrder.getId(), sourceOrder.getOrderNo(), "SPLIT", "PENDING_AUDIT", "CANCELLED", operator, "拆单后原订单取消");

        return newOrders;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrder mergeOrders(List<Long> orderIds, String operator) {
        List<OmsOrder> sourceOrders = orderMapper.selectBatchIds(orderIds);
        if (sourceOrders.isEmpty()) throw new BusinessException("订单不存在");

        OmsOrder first = sourceOrders.get(0);
        OmsOrder merged = cloneOrder(first, operator);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OmsOrder source : sourceOrders) {
            if (!"PENDING_AUDIT".equals(source.getOrderStatus())) {
                throw new BusinessException("订单" + source.getOrderNo() + "不能合并（状态不为待审核）");
            }
            List<OmsOrderItem> items = orderItemMapper.selectByOrderId(source.getId());
            for (OmsOrderItem item : items) {
                OmsOrderItem newItem = cloneItem(merged.getId(), item, item.getQuantity());
                orderItemMapper.insert(newItem);
                totalAmount = totalAmount.add(newItem.getTotalPrice());
            }
            // 取消原订单
            source.setOrderStatus("CANCELLED");
            source.setCancelReason("合单");
            source.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(source);
            addOrderLog(source.getId(), source.getOrderNo(), "MERGE", "PENDING_AUDIT", "CANCELLED", operator, "合单至" + merged.getOrderNo());
        }

        merged.setTotalAmount(totalAmount);
        merged.setPaymentAmount(totalAmount.add(merged.getShippingAmount()));
        orderMapper.updateById(merged);
        addOrderLog(merged.getId(), merged.getOrderNo(), "MERGE", null, "PENDING_AUDIT", operator, "合并订单");

        return merged;
    }

    @Override
    public void exportExcel(OrderQueryDTO dto, HttpServletResponse response) {
        try {
            List<OmsOrder> orders = orderMapper.selectList(
                    new LambdaQueryWrapper<OmsOrder>()
                            .like(StringUtils.hasText(dto.getOrderNo()), OmsOrder::getOrderNo, dto.getOrderNo())
                            .orderByDesc(OmsOrder::getCreateTime));

            List<OrderExcelVO> list = orders.stream().map(o -> {
                OrderExcelVO vo = new OrderExcelVO();
                vo.setOrderNo(o.getOrderNo());
                vo.setCustomerName(o.getCustomerName());
                vo.setPaymentAmount(o.getPaymentAmount());
                vo.setOrderStatus(o.getOrderStatus());
                vo.setReceiverName(o.getReceiverName());
                vo.setReceiverPhone(o.getReceiverPhone());
                vo.setCreateTime(o.getCreateTime());
                return vo;
            }).collect(Collectors.toList());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("订单导出", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), OrderExcelVO.class).sheet("订单数据").doWrite(list);
        } catch (Exception e) {
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    @Data
    static class OrderExcelVO {
        @ExcelProperty("订单号")
        private String orderNo;
        @ExcelProperty("客户")
        private String customerName;
        @ExcelProperty("金额")
        private BigDecimal paymentAmount;
        @ExcelProperty("状态")
        private String orderStatus;
        @ExcelProperty("收货人")
        private String receiverName;
        @ExcelProperty("联系电话")
        private String receiverPhone;
        @ExcelProperty("下单时间")
        private LocalDateTime createTime;
    }

    // ===== 内部辅助方法 =====

    private synchronized String generateOrderNo() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = ORDER_SEQ.getAndIncrement();
        return "ORD" + date + String.format("%06d", seq % 999999);
    }

    private OmsOrder cloneOrder(OmsOrder source, String operator) {
        OmsOrder order = new OmsOrder();
        order.setOrderNo(generateOrderNo());
        order.setCustomerId(source.getCustomerId());
        order.setCustomerName(source.getCustomerName());
        order.setCustomerPhone(source.getCustomerPhone());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setShippingAmount(source.getShippingAmount());
        order.setPaymentAmount(BigDecimal.ZERO);
        order.setOrderStatus("PENDING_AUDIT");
        order.setReceiverName(source.getReceiverName());
        order.setReceiverPhone(source.getReceiverPhone());
        order.setProvince(source.getProvince());
        order.setCity(source.getCity());
        order.setDistrict(source.getDistrict());
        order.setDetailAddress(source.getDetailAddress());
        order.setBuyerMessage(source.getBuyerMessage());
        order.setCreateBy(operator);
        orderMapper.insert(order);
        return order;
    }

    private OmsOrderItem cloneItem(Long newOrderId, OmsOrderItem source, Integer quantity) {
        OmsOrderItem item = new OmsOrderItem();
        item.setOrderId(newOrderId);
        item.setSkuId(source.getSkuId());
        item.setSkuCode(source.getSkuCode());
        item.setSkuName(source.getSkuName());
        item.setSkuSpec(source.getSkuSpec());
        item.setCategoryName(source.getCategoryName());
        item.setQuantity(quantity);
        item.setUnitPrice(source.getUnitPrice());
        item.setTotalPrice(source.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private void addOrderLog(Long orderId, String orderNo, String opType,
                             String before, String after, String operator, String content) {
        OmsOrderLog log = new OmsOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperationType(opType);
        log.setBeforeStatus(before);
        log.setAfterStatus(after);
        log.setOperator(operator);
        log.setOperationContent(content);
        orderLogMapper.insert(log);
    }
}
