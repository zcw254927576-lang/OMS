package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.entity.*;
import com.oms.exception.BusinessException;
import com.oms.mapper.*;
import com.oms.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final OmsInventoryMapper inventoryMapper;
    private final OmsInventoryLogMapper inventoryLogMapper;
    private final OmsOrderMapper orderMapper;
    private final OmsProductSkuMapper skuMapper;

    public InventoryServiceImpl(OmsInventoryMapper inventoryMapper,
                                OmsInventoryLogMapper inventoryLogMapper,
                                OmsOrderMapper orderMapper,
                                OmsProductSkuMapper skuMapper) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
        this.orderMapper = orderMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    public IPage<OmsInventory> pageQuery(Long warehouseId, String skuCode, int page, int pageSize) {
        Page<OmsInventory> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<OmsInventory> wrapper = new LambdaQueryWrapper<OmsInventory>()
                .eq(warehouseId != null, OmsInventory::getWarehouseId, warehouseId)
                .like(skuCode != null && !skuCode.isEmpty(), OmsInventory::getSkuCode, skuCode)
                .orderByDesc(OmsInventory::getUpdateTime);
        return inventoryMapper.selectPage(pg, wrapper);
    }

    @Override
    public List<OmsInventory> getLowStockList() {
        return inventoryMapper.selectLowStockList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long skuId, Integer quantity, String relatedNo, String operator) {
        int rows = inventoryMapper.lockStock(skuId, quantity);
        if (rows == 0) {
            OmsInventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<OmsInventory>().eq(OmsInventory::getSkuId, skuId));
            throw new BusinessException("库存不足：" + (inv != null ? inv.getSkuName() : skuId));
        }
        saveInventoryLog(skuId, "ORDER_LOCK", -quantity, relatedNo, operator, "下单锁定库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long skuId, Integer quantity, String relatedNo, String operator) {
        inventoryMapper.unlockStock(skuId, quantity);
        saveInventoryLog(skuId, "ORDER_UNLOCK", quantity, relatedNo, operator, "取消订单释放库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long skuId, Integer quantity, String relatedNo, String operator) {
        int rows = inventoryMapper.deductStock(skuId, quantity);
        if (rows == 0) {
            throw new BusinessException("扣减库存失败，锁定库存不足：" + skuId);
        }
        saveInventoryLog(skuId, "OUT", -quantity, relatedNo, operator, "发货扣减库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnStock(Long skuId, Integer quantity, String relatedNo, String operator) {
        inventoryMapper.returnStock(skuId, quantity);
        saveInventoryLog(skuId, "RETURN", quantity, relatedNo, operator, "退货回冲库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(Long skuId, Integer quantity, String remark, String operator) {
        OmsInventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<OmsInventory>().eq(OmsInventory::getSkuId, skuId));
        if (inv == null) {
            OmsProductSku sku = skuMapper.selectById(skuId);
            if (sku == null) throw new BusinessException("SKU不存在");
            inv = new OmsInventory();
            inv.setSkuId(skuId);
            inv.setSkuCode(sku.getSkuCode());
            inv.setSkuName(sku.getSkuName());
            inv.setQuantity(quantity);
            inv.setLockedQuantity(0);
            inv.setAvailableQuantity(quantity);
            inventoryMapper.insert(inv);
        } else {
            inventoryMapper.updateById(inv);
            inv.setQuantity(inv.getQuantity() + quantity);
            inv.setAvailableQuantity(inv.getQuantity() - inv.getLockedQuantity());
            inventoryMapper.updateById(inv);
        }
        saveInventoryLog(skuId, "ADJUST", quantity, null, operator, remark);
    }

    @Override
    public IPage<OmsInventoryLog> logPage(Long skuId, int page, int pageSize) {
        Page<OmsInventoryLog> pg = new Page<>(page, pageSize);
        return inventoryLogMapper.selectPage(pg,
                new LambdaQueryWrapper<OmsInventoryLog>()
                        .eq(skuId != null, OmsInventoryLog::getSkuId, skuId)
                        .orderByDesc(OmsInventoryLog::getCreateTime));
    }

    // ===== 库存盘点 =====

    private final Map<Long, OmsStockTake> stockTakeMap = new HashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsStockTake createStockTake(Long warehouseId, String operator) {
        OmsStockTake take = new OmsStockTake();
        take.setTakeNo("TAKE" + System.currentTimeMillis());
        take.setWarehouseId(warehouseId);
        take.setStatus(0);
        take.setOperator(operator);
        return take;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeStockTake(Long takeId, List<OmsStockTakeItemForm> items, String operator) {
        int profitCount = 0, lossCount = 0;
        for (OmsStockTakeItemForm item : items) {
            OmsInventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<OmsInventory>().eq(OmsInventory::getSkuId, item.skuId));
            if (inv == null) continue;
            int diff = item.actualQuantity - inv.getQuantity();
            if (diff != 0) {
                if (diff > 0) profitCount++;
                else lossCount++;
                // 调整库存
                inv.setQuantity(item.actualQuantity);
                inv.setAvailableQuantity(item.actualQuantity - inv.getLockedQuantity());
                inventoryMapper.updateById(inv);
                saveInventoryLog(item.skuId, "ADJUST", diff, null, operator,
                        "盘点调整，差异：" + diff + "，备注：" + (item.remark != null ? item.remark : ""));
            }
        }
    }

    @Override
    public IPage<OmsStockTake> stockTakePage(int page, int pageSize) {
        return new Page<>(page, pageSize);
    }

    @Override
    public List<Object> getStockTurnover() {
        return Collections.emptyList();
    }

    /** 记录库存变动日志 */
    private void saveInventoryLog(Long skuId, String changeType, Integer quantity,
                                  String relatedNo, String operator, String remark) {
        OmsInventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<OmsInventory>().eq(OmsInventory::getSkuId, skuId));

        OmsInventoryLog log = new OmsInventoryLog();
        log.setSkuId(skuId);
        log.setSkuCode(inv != null ? inv.getSkuCode() : null);
        log.setChangeType(changeType);
        log.setQuantity(quantity);
        log.setBeforeQuantity(inv != null ? inv.getQuantity() - (quantity > 0 ? quantity : 0) : 0);
        log.setAfterQuantity(inv != null ? inv.getQuantity() : 0);
        log.setRelatedNo(relatedNo);
        log.setOperator(operator);
        log.setRemark(remark);
        inventoryLogMapper.insert(log);
    }
}
