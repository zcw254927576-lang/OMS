package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.OmsCustomer;
import com.oms.entity.OmsCustomerLevel;
import java.util.List;
import java.util.Map;

/**
 * 客户服务接口
 */
public interface CustomerService {
    /** 分页查询 */
    IPage<OmsCustomer> pageQuery(String keyword, Long levelId, int page, int pageSize);

    /** 客户详情 */
    OmsCustomer getById(Long id);

    /** 新增客户 */
    void saveCustomer(OmsCustomer customer);

    /** 更新客户 */
    void updateCustomer(OmsCustomer customer);

    /** 客户统计 */
    Map<String, Object> getCustomerStats(Long customerId);

    /** 客户排行 */
    List<Map<String, Object>> getCustomerRanking(int limit);

    /** 客户等级列表 */
    List<OmsCustomerLevel> levelList();

    /** 新增/更新等级 */
    void saveLevel(OmsCustomerLevel level);

    /** 复购率统计 */
    Map<String, Object> getRepurchaseStats();
}
