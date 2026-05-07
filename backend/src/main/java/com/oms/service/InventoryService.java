package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.OmsInventory;
import com.oms.entity.OmsInventoryLog;
import com.oms.entity.OmsStockTake;
import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {
    /** 分页查询库存 */
    IPage<OmsInventory> pageQuery(Long warehouseId, String skuCode, int page, int pageSize);

    /** 库存预警列表 */
    List<OmsInventory> getLowStockList();

    /** 下单锁定库存 */
    void lockStock(Long skuId, Integer quantity, String relatedNo, String operator);

    /** 取消订单释放库存 */
    void unlockStock(Long skuId, Integer quantity, String relatedNo, String operator);

    /** 发货扣减库存 */
    void deductStock(Long skuId, Integer quantity, String relatedNo, String operator);

    /** 退货回冲库存 */
    void returnStock(Long skuId, Integer quantity, String relatedNo, String operator);

    /** 库存调整 */
    void adjustStock(Long skuId, Integer quantity, String remark, String operator);

    /** 库存变动日志 */
    IPage<OmsInventoryLog> logPage(Long skuId, int page, int pageSize);

    /** 创建盘点单 */
    OmsStockTake createStockTake(Long warehouseId, String operator);

    /** 完成盘点 */
    void completeStockTake(Long takeId, List<OmsStockTakeItemForm> items, String operator);

    /** 盘点记录 */
    IPage<OmsStockTake> stockTakePage(int page, int pageSize);

    /** 库存周转率 */
    List<Object> getStockTurnover();

    class OmsStockTakeItemForm {
        public Long skuId;
        public Integer actualQuantity;
        public String remark;
    }
}
