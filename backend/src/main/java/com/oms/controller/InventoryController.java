package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.entity.OmsInventory;
import com.oms.entity.OmsInventoryLog;
import com.oms.service.InventoryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/page")
    public R<IPage<OmsInventory>> page(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String skuCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(inventoryService.pageQuery(warehouseId, skuCode, page, pageSize));
    }

    @GetMapping("/low-stock")
    public R<List<OmsInventory>> lowStock() {
        return R.ok(inventoryService.getLowStockList());
    }

    @PostMapping("/adjust")
    public R<Void> adjust(@RequestBody Map<String, Object> body, Authentication auth) {
        Long skuId = Long.valueOf(body.get("skuId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        String remark = (String) body.get("remark");
        inventoryService.adjustStock(skuId, quantity, remark, (String) auth.getCredentials());
        return R.ok();
    }

    @GetMapping("/log")
    public R<IPage<OmsInventoryLog>> logPage(
            @RequestParam(required = false) Long skuId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(inventoryService.logPage(skuId, page, pageSize));
    }

    @GetMapping("/turnover")
    public R<List<Object>> turnover() {
        return R.ok(inventoryService.getStockTurnover());
    }
}
