package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.entity.OmsCustomer;
import com.oms.entity.OmsCustomerLevel;
import com.oms.mapper.OmsCustomerLevelMapper;
import com.oms.mapper.OmsCustomerMapper;
import com.oms.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final OmsCustomerMapper customerMapper;
    private final OmsCustomerLevelMapper levelMapper;

    public CustomerServiceImpl(OmsCustomerMapper customerMapper, OmsCustomerLevelMapper levelMapper) {
        this.customerMapper = customerMapper;
        this.levelMapper = levelMapper;
    }

    @Override
    public IPage<OmsCustomer> pageQuery(String keyword, Long levelId, int page, int pageSize) {
        Page<OmsCustomer> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<OmsCustomer> wrapper = new LambdaQueryWrapper<OmsCustomer>()
                .and(StringUtils.hasText(keyword), w -> w
                        .like(OmsCustomer::getCustomerName, keyword)
                        .or().like(OmsCustomer::getPhone, keyword)
                        .or().like(OmsCustomer::getCustomerNo, keyword))
                .eq(levelId != null, OmsCustomer::getLevelId, levelId)
                .orderByDesc(OmsCustomer::getCreateTime);
        return customerMapper.selectPage(pg, wrapper);
    }

    @Override
    public OmsCustomer getById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    @Transactional
    public void saveCustomer(OmsCustomer customer) {
        if (!StringUtils.hasText(customer.getCustomerNo())) {
            customer.setCustomerNo(generateCustomerNo());
        }
        customerMapper.insert(customer);
    }

    private String generateCustomerNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randPart = new Random().nextInt(9999);
        return "CUS" + datePart + String.format("%04d", randPart);
    }

    @Override
    @Transactional
    public void updateCustomer(OmsCustomer customer) {
        customerMapper.updateById(customer);
    }

    @Override
    public Map<String, Object> getCustomerStats(Long customerId) {
        return customerMapper.selectCustomerStats(customerId);
    }

    @Override
    public List<Map<String, Object>> getCustomerRanking(int limit) {
        return customerMapper.selectCustomerRanking(limit);
    }

    @Override
    public List<OmsCustomerLevel> levelList() {
        return levelMapper.selectList(null);
    }

    @Override
    @Transactional
    public void saveLevel(OmsCustomerLevel level) {
        if (level.getId() == null) {
            levelMapper.insert(level);
        } else {
            levelMapper.updateById(level);
        }
    }

    @Override
    public Map<String, Object> getRepurchaseStats() {
        return customerMapper.selectRepurchaseStats();
    }
}
