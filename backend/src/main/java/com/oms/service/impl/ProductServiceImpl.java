package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.entity.OmsInventory;
import com.oms.entity.OmsProductCategory;
import com.oms.entity.OmsProductSku;
import com.oms.exception.BusinessException;
import com.oms.mapper.OmsInventoryMapper;
import com.oms.mapper.OmsProductCategoryMapper;
import com.oms.mapper.OmsProductSkuMapper;
import com.oms.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final OmsProductSkuMapper skuMapper;
    private final OmsProductCategoryMapper categoryMapper;
    private final OmsInventoryMapper inventoryMapper;

    public ProductServiceImpl(OmsProductSkuMapper skuMapper,
                              OmsProductCategoryMapper categoryMapper,
                              OmsInventoryMapper inventoryMapper) {
        this.skuMapper = skuMapper;
        this.categoryMapper = categoryMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public IPage<OmsProductSku> pageSku(String keyword, Long categoryId, int page, int pageSize) {
        Page<OmsProductSku> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<OmsProductSku> wrapper = new LambdaQueryWrapper<OmsProductSku>()
                .like(StringUtils.hasText(keyword), OmsProductSku::getSkuName, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), OmsProductSku::getSkuCode, keyword)
                .eq(categoryId != null, OmsProductSku::getCategoryId, categoryId)
                .orderByDesc(OmsProductSku::getCreateTime);
        return skuMapper.selectPage(pg, wrapper);
    }

    @Override
    public OmsProductSku getSkuById(Long id) {
        return skuMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSku(OmsProductSku sku) {
        skuMapper.insert(sku);
        // 创建初始库存记录
        OmsInventory inv = new OmsInventory();
        inv.setSkuId(sku.getId());
        inv.setSkuCode(sku.getSkuCode());
        inv.setSkuName(sku.getSkuName());
        inv.setQuantity(0);
        inv.setLockedQuantity(0);
        inv.setAvailableQuantity(0);
        inv.setMinStock(10);
        inv.setMaxStock(99999);
        inventoryMapper.insert(inv);
    }

    @Override
    @Transactional
    public void updateSku(OmsProductSku sku) {
        skuMapper.updateById(sku);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        OmsProductSku sku = skuMapper.selectById(id);
        if (sku == null) throw new BusinessException("SKU不存在");
        sku.setStatus(sku.getStatus() == 1 ? 0 : 1);
        skuMapper.updateById(sku);
    }

    @Override
    public List<OmsProductCategory> categoryList() {
        List<OmsProductCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<OmsProductCategory>().orderByAsc(OmsProductCategory::getSort));
        // 构建树形
        List<OmsProductCategory> tree = new ArrayList<>();
        for (OmsProductCategory cat : all) {
            if (cat.getParentId() == null || cat.getParentId() == 0) {
                tree.add(cat);
            }
        }
        return tree;
    }

    @Override
    @Transactional
    public void saveCategory(OmsProductCategory category) {
        categoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void updateCategory(OmsProductCategory category) {
        categoryMapper.updateById(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        long count = skuMapper.selectCount(
                new LambdaQueryWrapper<OmsProductSku>().eq(OmsProductSku::getCategoryId, id));
        if (count > 0) {
            throw new BusinessException("该分类下有" + count + "个商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
