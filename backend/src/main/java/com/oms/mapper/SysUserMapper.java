package com.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oms.entity.SysUser;
import org.apache.ibatis.annotations.Select;
import java.util.Set;

/**
 * 用户 Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    /** 查询用户权限标识集合 */
    @Select("SELECT DISTINCT m.perms FROM sys_user_role ur " +
            "JOIN sys_role_menu rm ON ur.role_id = rm.role_id " +
            "JOIN sys_menu m ON rm.menu_id = m.id " +
            "WHERE ur.user_id = #{userId} AND m.perms IS NOT NULL AND m.perms != ''")
    Set<String> selectUserPerms(Long userId);
}
