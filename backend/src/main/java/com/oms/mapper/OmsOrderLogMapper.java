package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.OmsOrderLog;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface OmsOrderLogMapper extends BaseMapper<OmsOrderLog> {
    @Select("SELECT * FROM oms_order_log WHERE order_id = #{orderId} ORDER BY create_time ASC")
    List<OmsOrderLog> selectByOrderId(Long orderId);
}
