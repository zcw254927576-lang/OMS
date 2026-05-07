package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.OmsOrderItem;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface OmsOrderItemMapper extends BaseMapper<OmsOrderItem> {
    @Select("SELECT * FROM oms_order_item WHERE order_id = #{orderId}")
    List<OmsOrderItem> selectByOrderId(Long orderId);
}
