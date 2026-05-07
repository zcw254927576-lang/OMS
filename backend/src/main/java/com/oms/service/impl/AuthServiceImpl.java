package com.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oms.config.JwtUtil;
import com.oms.dto.LoginDTO;
import com.oms.entity.SysMenu;
import com.oms.entity.SysUser;
import com.oms.exception.BusinessException;
import com.oms.mapper.SysMenuMapper;
import com.oms.mapper.SysUserMapper;
import com.oms.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(SysUserMapper userMapper, SysMenuMapper menuMapper,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.menuMapper = menuMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return result;
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        Set<String> perms = userMapper.selectUserPerms(userId);

        // 构建菜单树
        List<Map<String, Object>> menuTree = buildMenuTree(menus);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("avatar", user.getAvatar());

        result.put("user", userInfo);
        result.put("menus", menuTree);
        result.put("perms", perms);
        return result;
    }

    /** 构建菜单树（递归） */
    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menus) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                result.add(menuToMap(menu, menus));
            }
        }
        result.sort(Comparator.comparingInt(m -> (int) m.get("sort")));
        return result;
    }

    private Map<String, Object> menuToMap(SysMenu menu, List<SysMenu> all) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", menu.getId());
        map.put("menuName", menu.getMenuName());
        map.put("menuType", menu.getMenuType());
        map.put("path", menu.getPath());
        map.put("component", menu.getComponent());
        map.put("perms", menu.getPerms());
        map.put("icon", menu.getIcon());
        map.put("sort", menu.getSort());

        List<Map<String, Object>> children = new ArrayList<>();
        for (SysMenu m : all) {
            if (menu.getId().equals(m.getParentId())) {
                children.add(menuToMap(m, all));
            }
        }
        children.sort(Comparator.comparingInt(c -> (int) c.get("sort")));
        map.put("children", children);
        return map;
    }
}
