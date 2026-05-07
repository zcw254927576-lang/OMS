package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.dto.PurchaseOrderCreateDTO;
import com.oms.entity.*;
import com.oms.exception.BusinessException;
import com.oms.mapper.*;
import com.oms.service.PurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;
    private final OmsInventoryMapper inventoryMapper;
    private final OmsInventoryLogMapper inventoryLogMapper;
    private final OmsProductSkuMapper skuMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderMapper orderMapper,
                                    PurchaseOrderItemMapper itemMapper,
                                    OmsInventoryMapper inventoryMapper,
                                    OmsInventoryLogMapper inventoryLogMapper,
                                    OmsProductSkuMapper skuMapper) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    public IPage<PurchaseOrder> pageQuery(String keyword, int page, int pageSize) {
        Page<PurchaseOrder> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<PurchaseOrder>()
                .and(StringUtils.hasText(keyword), w -> w
                        .like(PurchaseOrder::getOrderNo, keyword)
                        .or().like(PurchaseOrder::getSupplierName, keyword))
                .orderByDesc(PurchaseOrder::getCreateTime);
        return orderMapper.selectPage(pg, wrapper);
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<PurchaseOrderItem> getItems(Long orderId) {
        return itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>()
                        .eq(PurchaseOrderItem::getOrderId, orderId)
                        .orderByAsc(PurchaseOrderItem::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(PurchaseOrderCreateDTO dto, String createBy) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setSupplierName(dto.getSupplierName());
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        order.setCreateBy(createBy);
        orderMapper.insert(order);

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderCreateDTO.Item itemDto : dto.getItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setOrderId(order.getId());
            item.setSkuId(itemDto.getSkuId());
            item.setSkuName(itemDto.getSkuName());
            item.setQuantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 0);
            item.setUnitPrice(itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : BigDecimal.ZERO);
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemMapper.insert(item);
            total = total.add(item.getTotalPrice());
        }

        order.setTotalAmount(total);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("采购单不存在");
        if (order.getStatus() != 0) throw new BusinessException("当前状态不可提交");
        order.setStatus(1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("采购单不存在");
        if (order.getStatus() != 1) throw new BusinessException("当前状态不可审核");
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void warehouse(Long id, String operator) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("采购单不存在");
        if (order.getStatus() != 2) throw new BusinessException("当前状态不可入库");

        List<PurchaseOrderItem> items = getItems(id);
        for (PurchaseOrderItem item : items) {
            if (item.getSkuId() == null) continue;
            // 查找或创建库存记录
            OmsInventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<OmsInventory>()
                            .eq(OmsInventory::getSkuId, item.getSkuId()));
            if (inv == null) {
                OmsProductSku sku = skuMapper.selectById(item.getSkuId());
                inv = new OmsInventory();
                inv.setSkuId(item.getSkuId());
                inv.setSkuCode(sku != null ? sku.getSkuCode() : "");
                inv.setSkuName(item.getSkuName());
                inv.setQuantity(item.getQuantity());
                inv.setLockedQuantity(0);
                inv.setAvailableQuantity(item.getQuantity());
                inventoryMapper.insert(inv);
            } else {
                inv.setQuantity(inv.getQuantity() + item.getQuantity());
                inv.setAvailableQuantity(inv.getQuantity() - inv.getLockedQuantity());
                inventoryMapper.updateById(inv);
            }
            // 记录库存变动日志
            OmsInventoryLog log = new OmsInventoryLog();
            log.setSkuId(item.getSkuId());
            log.setSkuCode(inv.getSkuCode());
            log.setChangeType("PURCHASE_IN");
            log.setQuantity(item.getQuantity());
            log.setBeforeQuantity(inv.getQuantity() - item.getQuantity());
            log.setAfterQuantity(inv.getQuantity());
            log.setRelatedNo(order.getOrderNo());
            log.setOperator(operator);
            log.setRemark("采购入库：" + order.getOrderNo());
            inventoryLogMapper.insert(log);
        }

        order.setStatus(3);
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randPart = new Random().nextInt(9999);
        return "PO" + datePart + String.format("%04d", randPart);
    }
}
