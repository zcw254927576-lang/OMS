package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.entity.OmsCustomer;
import com.oms.entity.OmsCustomerLevel;
import com.oms.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/page")
    public R<IPage<OmsCustomer>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(customerService.pageQuery(keyword, levelId, page, pageSize));
    }

    @GetMapping("/{id}")
    public R<OmsCustomer> detail(@PathVariable Long id) {
        return R.ok(customerService.getById(id));
    }

    @PostMapping
    public R<Void> save(@RequestBody OmsCustomer customer) {
        customerService.saveCustomer(customer);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody OmsCustomer customer) {
        customerService.updateCustomer(customer);
        return R.ok();
    }

    @GetMapping("/{id}/stats")
    public R<Map<String, Object>> stats(@PathVariable Long id) {
        return R.ok(customerService.getCustomerStats(id));
    }

    @GetMapping("/ranking")
    public R<List<Map<String, Object>>> ranking(@RequestParam(defaultValue = "10") int limit) {
        return R.ok(customerService.getCustomerRanking(limit));
    }

    @GetMapping("/repurchase")
    public R<Map<String, Object>> repurchase() {
        return R.ok(customerService.getRepurchaseStats());
    }

    // ===== 客户等级 =====

    @GetMapping("/level/list")
    public R<List<OmsCustomerLevel>> levelList() {
        return R.ok(customerService.levelList());
    }

    @PostMapping("/level")
    public R<Void> saveLevel(@RequestBody OmsCustomerLevel level) {
        customerService.saveLevel(level);
        return R.ok();
    }
}
