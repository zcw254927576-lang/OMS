package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.SysMenu;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /** 查询用户的菜单（通过角色关联） */
    @Select("SELECT DISTINCT m.* FROM sys_user_role ur " +
            "JOIN sys_role_menu rm ON ur.role_id = rm.role_id " +
            "JOIN sys_menu m ON rm.menu_id = m.id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 ORDER BY m.sort")
    List<SysMenu> selectMenusByUserId(Long userId);
}
