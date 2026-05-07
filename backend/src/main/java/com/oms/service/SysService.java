package com.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.entity.*;
import java.util.List;

/**
 * 系统管理服务接口
 */
public interface SysService {
    /** 用户分页 */
    IPage<SysUser> pageUser(String keyword, int page, int pageSize);

    /** 新增用户 */
    void saveUser(SysUser user);

    /** 更新用户 */
    void updateUser(SysUser user);

    /** 重置密码 */
    void resetPassword(Long userId);

    /** 角色列表 */
    List<SysRole> listRole();

    /** 新增角色 */
    void saveRole(SysRole role);

    /** 分配角色菜单 */
    void assignRoleMenus(Long roleId, List<Long> menuIds);

    /** 菜单树 */
    List<SysMenu> menuTree();

    /** 获取用户菜单 */
    List<SysMenu> getUserMenus(Long userId);

    /** 操作日志分页 */
    IPage<SysOperationLog> pageLog(String module, int page, int pageSize);
}
