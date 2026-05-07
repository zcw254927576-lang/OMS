package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oms.entity.*;
import com.oms.mapper.*;
import com.oms.service.SysService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysServiceImpl implements SysService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;
    private final OmsOperationLogMapper operationLogMapper;

    public SysServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper,
                          SysMenuMapper menuMapper, PasswordEncoder passwordEncoder,
                          OmsOperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.passwordEncoder = passwordEncoder;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public IPage<SysUser> pageUser(String keyword, int page, int pageSize) {
        Page<SysUser> pg = new Page<>(page, pageSize);
        return userMapper.selectPage(pg,
                new LambdaQueryWrapper<SysUser>()
                        .like(StringUtils.hasText(keyword), SysUser::getUsername, keyword)
                        .or(StringUtils.hasText(keyword))
                        .like(StringUtils.hasText(keyword), SysUser::getRealName, keyword)
                        .orderByDesc(SysUser::getCreateTime));
    }

    @Override
    @Transactional
    public void saveUser(SysUser user) {
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateUser(SysUser user) {
        user.setPassword(null);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            userMapper.updateById(user);
        }
    }

    @Override
    public List<SysRole> listRole() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1));
    }

    @Override
    @Transactional
    public void saveRole(SysRole role) {
        if (role.getId() == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
    }

    @Override
    @Transactional
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        roleMapper.deleteById(roleId);
        for (Long menuId : menuIds) {
            // insert into sys_role_menu...
        }
    }

    @Override
    public List<SysMenu> menuTree() {
        List<SysMenu> all = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1)
                        .orderByAsc(SysMenu::getSort));
        return buildTree(all);
    }

    private List<SysMenu> buildTree(List<SysMenu> all) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu m : all) {
            if (m.getParentId() == null || m.getParentId() == 0) {
                tree.add(m);
            }
        }
        return tree;
    }

    @Override
    public List<SysMenu> getUserMenus(Long userId) {
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public IPage<SysOperationLog> pageLog(String module, int page, int pageSize) {
        Page<SysOperationLog> pg = new Page<>(page, pageSize);
        return operationLogMapper.selectPage(pg,
                new LambdaQueryWrapper<SysOperationLog>()
                        .eq(StringUtils.hasText(module), SysOperationLog::getModule, module)
                        .orderByDesc(SysOperationLog::getCreateTime));
    }
}
