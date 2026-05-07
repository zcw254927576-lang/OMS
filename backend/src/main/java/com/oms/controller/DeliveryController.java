package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.dto.DeliveryDTO;
import com.oms.entity.OmsDeliveryOrder;
import com.oms.entity.OmsAfterSale;
import com.oms.mapper.OmsAfterSaleMapper;
import com.oms.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流发货控制器
 */
@RestController
@RequestMapping("/api/logistics")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final OmsAfterSaleMapper afterSaleMapper;

    public DeliveryController(DeliveryService deliveryService, OmsAfterSaleMapper afterSaleMapper) {
        this.deliveryService = deliveryService;
        this.afterSaleMapper = afterSaleMapper;
    }

    /** 发货单列表 */
    @GetMapping("/delivery/page")
    public R<IPage<OmsDeliveryOrder>> deliveryPage(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(deliveryService.pageQuery(orderNo, status, page, pageSize));
    }

    /** 发货 */
    @PostMapping("/delivery")
    public R<Void> delivery(@Valid @RequestBody DeliveryDTO dto, Authentication auth) {
        deliveryService.delivery(dto, (String) auth.getCredentials());
        return R.ok();
    }

    /** 批量发货 */
    @PostMapping("/delivery/batch")
    public R<Void> batchDelivery(@Valid @RequestBody List<DeliveryDTO> list, Authentication auth) {
        deliveryService.batchDelivery(list, (String) auth.getCredentials());
        return R.ok();
    }

    // ===== 售后管理 =====

    @GetMapping("/after-sale/page")
    public R<IPage<OmsAfterSale>> afterSalePage(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(afterSaleMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OmsAfterSale>()
                        .eq(status != null && !status.isEmpty(), OmsAfterSale::getStatus, status)
                        .orderByDesc(OmsAfterSale::getCreateTime)));
    }

    @GetMapping("/after-sale/{id}")
    public R<OmsAfterSale> afterSaleDetail(@PathVariable Long id) {
        return R.ok(afterSaleMapper.selectById(id));
    }

    @PostMapping("/after-sale")
    public R<Void> createAfterSale(@RequestBody OmsAfterSale afterSale, Authentication auth) {
        afterSale.setApplicant((String) auth.getCredentials());
        afterSale.setStatus("PENDING");
        afterSaleMapper.insert(afterSale);
        return R.ok();
    }

    @PutMapping("/after-sale/{id}/audit")
    public R<Void> auditAfterSale(@PathVariable Long id, @RequestBody com.oms.dto.AuditDTO dto, Authentication auth) {
        deliveryService.auditAfterSale(id, dto.isApproved(), dto.getRemark(), (String) auth.getCredentials());
        return R.ok();
    }
}
