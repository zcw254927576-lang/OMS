package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.OmsProductCategory;
import com.oms.entity.OmsProductSku;
import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    /** SKU分页查询 */
    IPage<OmsProductSku> pageSku(String keyword, Long categoryId, int page, int pageSize);

    /** SKU详情 */
    OmsProductSku getSkuById(Long id);

    /** 新增SKU */
    void saveSku(OmsProductSku sku);

    /** 更新SKU */
    void updateSku(OmsProductSku sku);

    /** 上下架 */
    void toggleStatus(Long id);

    /** 分类列表（树形） */
    List<OmsProductCategory> categoryList();

    /** 新增分类 */
    void saveCategory(OmsProductCategory category);

    /** 更新分类 */
    void updateCategory(OmsProductCategory category);

    /** 删除分类 */
    void deleteCategory(Long id);
}
