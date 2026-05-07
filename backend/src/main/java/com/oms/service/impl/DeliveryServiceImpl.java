package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.dto.DeliveryDTO;
import com.oms.entity.*;
import com.oms.exception.BusinessException;
import com.oms.mapper.*;
import com.oms.service.DeliveryService;
import com.oms.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final OmsDeliveryOrderMapper deliveryMapper;
    private final OmsOrderMapper orderMapper;
    private final OmsOrderItemMapper orderItemMapper;
    private final OmsAfterSaleMapper afterSaleMapper;
    private final OmsOrderLogMapper orderLogMapper;
    private final InventoryService inventoryService;

    public DeliveryServiceImpl(OmsDeliveryOrderMapper deliveryMapper, OmsOrderMapper orderMapper,
                               OmsOrderItemMapper orderItemMapper, OmsAfterSaleMapper afterSaleMapper,
                               OmsOrderLogMapper orderLogMapper, InventoryService inventoryService) {
        this.deliveryMapper = deliveryMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.afterSaleMapper = afterSaleMapper;
        this.orderLogMapper = orderLogMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    public IPage<OmsDeliveryOrder> pageQuery(String orderNo, String status, int page, int pageSize) {
        Page<OmsDeliveryOrder> pg = new Page<>(page, pageSize);
        return deliveryMapper.selectPage(pg,
                new LambdaQueryWrapper<OmsDeliveryOrder>()
                        .like(StringUtils.hasText(orderNo), OmsDeliveryOrder::getOrderNo, orderNo)
                        .eq(StringUtils.hasText(status), OmsDeliveryOrder::getStatus, status)
                        .orderByDesc(OmsDeliveryOrder::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delivery(DeliveryDTO dto, String operator) {
        OmsOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new BusinessException("订单不存在");
        if (!"PENDING_DELIVERY".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不为待发货");
        }

        // 创建发货单
        OmsDeliveryOrder delivery = new OmsDeliveryOrder();
        delivery.setDeliveryNo("DNO" + System.currentTimeMillis());
        delivery.setOrderNo(order.getOrderNo());
        delivery.setOrderId(order.getId());
        delivery.setExpressCompany(dto.getExpressCompany());
        delivery.setExpressNo(dto.getExpressNo());
        delivery.setReceiverName(order.getReceiverName());
        delivery.setReceiverPhone(order.getReceiverPhone());
        delivery.setReceiverAddress(order.getProvince() + order.getCity() + order.getDistrict() + order.getDetailAddress());
        delivery.setStatus("DELIVERED");
        delivery.setOperator(operator);
        delivery.setDeliveryTime(LocalDateTime.now());
        deliveryMapper.insert(delivery);

        // 更新订单状态
        order.setOrderStatus("DELIVERED");
        order.setExpressCompany(dto.getExpressCompany());
        order.setExpressNo(dto.getExpressNo());
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 扣减库存
        List<OmsOrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OmsOrderItem item : items) {
            inventoryService.deductStock(item.getSkuId(), item.getQuantity(), order.getOrderNo(), operator);
        }

        // 记录日志
        OmsOrderLog log = new OmsOrderLog();
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setOperationType("DELIVERY");
        log.setBeforeStatus("PENDING_DELIVERY");
        log.setAfterStatus("DELIVERED");
        log.setOperator(operator);
        log.setOperationContent("发货：" + dto.getExpressCompany() + " " + dto.getExpressNo());
        orderLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelivery(List<DeliveryDTO> list, String operator) {
        for (DeliveryDTO dto : list) {
            delivery(dto, operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditAfterSale(Long afterSaleId, boolean approved, String remark, String operator) {
        OmsAfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) throw new BusinessException("售后单不存在");

        if (approved) {
            afterSale.setStatus("APPROVED");
            afterSale.setApproveTime(LocalDateTime.now());
            afterSale.setApproveRemark(remark);

            // 退货回冲库存
            if ("RETURN".equals(afterSale.getAfterSaleType()) || "REFUND".equals(afterSale.getAfterSaleType())) {
                inventoryService.returnStock(afterSale.getSkuId(), afterSale.getQuantity(), afterSale.getOrderNo(), operator);
            }
        } else {
            afterSale.setStatus("REJECTED");
            afterSale.setApproveRemark(remark);
        }
        afterSaleMapper.updateById(afterSale);
    }
}
