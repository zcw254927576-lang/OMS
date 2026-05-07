package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.dto.PurchaseOrderCreateDTO;
import com.oms.entity.PurchaseOrder;
import com.oms.entity.PurchaseOrderItem;
import com.oms.service.PurchaseOrderService;
import com.oms.common.R;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseService;

    public PurchaseOrderController(PurchaseOrderService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/page")
    public R<IPage<PurchaseOrder>> page(@RequestBody Map<String, Object> params) {
        String keyword = (String) params.getOrDefault("keyword", "");
        int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "20").toString());
        return R.ok(purchaseService.pageQuery(keyword, page, pageSize));
    }

    @GetMapping("/{id}")
    public R<PurchaseOrder> getById(@PathVariable Long id) {
        return R.ok(purchaseService.getById(id));
    }

    @GetMapping("/{id}/items")
    public R<List<PurchaseOrderItem>> getItems(@PathVariable Long id) {
        return R.ok(purchaseService.getItems(id));
    }

    @PostMapping
    public R<Void> create(@Valid @RequestBody PurchaseOrderCreateDTO dto) {
        purchaseService.createOrder(dto, "admin");
        return R.ok();
    }

    @PutMapping("/{id}/submit")
    public R<Void> submit(@PathVariable Long id) {
        purchaseService.submit(id);
        return R.ok();
    }

    @PutMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id) {
        purchaseService.approve(id);
        return R.ok();
    }

    @PutMapping("/{id}/warehouse")
    public R<Void> warehouse(@PathVariable Long id) {
        purchaseService.warehouse(id, "admin");
        return R.ok();
    }
}
