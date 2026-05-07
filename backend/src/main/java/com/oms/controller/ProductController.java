package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.entity.OmsProductCategory;
import com.oms.entity.OmsProductSku;
import com.oms.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ===== SKU管理 =====

    @GetMapping("/sku/page")
    public R<IPage<OmsProductSku>> skuPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(productService.pageSku(keyword, categoryId, page, pageSize));
    }

    @GetMapping("/sku/{id}")
    public R<OmsProductSku> skuDetail(@PathVariable Long id) {
        return R.ok(productService.getSkuById(id));
    }

    @PostMapping("/sku")
    public R<Void> saveSku(@RequestBody OmsProductSku sku) {
        productService.saveSku(sku);
        return R.ok();
    }

    @PutMapping("/sku")
    public R<Void> updateSku(@RequestBody OmsProductSku sku) {
        productService.updateSku(sku);
        return R.ok();
    }

    @PutMapping("/sku/{id}/toggle-status")
    public R<Void> toggleStatus(@PathVariable Long id) {
        productService.toggleStatus(id);
        return R.ok();
    }

    // ===== 分类管理 =====

    @GetMapping("/category")
    public R<List<OmsProductCategory>> categoryList() {
        return R.ok(productService.categoryList());
    }

    @PostMapping("/category")
    public R<Void> saveCategory(@RequestBody OmsProductCategory category) {
        productService.saveCategory(category);
        return R.ok();
    }

    @PutMapping("/category")
    public R<Void> updateCategory(@RequestBody OmsProductCategory category) {
        productService.updateCategory(category);
        return R.ok();
    }

    @DeleteMapping("/category/{id}")
    public R<Void> deleteCategory(@PathVariable Long id) {
        productService.deleteCategory(id);
        return R.ok();
    }
}
