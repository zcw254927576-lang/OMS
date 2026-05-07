package com.oms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.oms.common.R;
import com.oms.entity.*;
import com.oms.service.SysService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/api/system")
public class SysController {

    private final SysService sysService;

    public SysController(SysService sysService) {
        this.sysService = sysService;
    }

    // ===== 用户管理 =====

    @GetMapping("/user/page")
    public R<IPage<SysUser>> userPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(sysService.pageUser(keyword, page, pageSize));
    }

    @PostMapping("/user")
    public R<Void> saveUser(@RequestBody SysUser user) {
        sysService.saveUser(user);
        return R.ok();
    }

    @PutMapping("/user")
    public R<Void> updateUser(@RequestBody SysUser user) {
        sysService.updateUser(user);
        return R.ok();
    }

    @PutMapping("/user/{id}/reset-pwd")
    public R<Void> resetPassword(@PathVariable Long id) {
        sysService.resetPassword(id);
        return R.ok();
    }

    // ===== 角色管理 =====

    @GetMapping("/role")
    public R<List<SysRole>> roleList() {
        return R.ok(sysService.listRole());
    }

    @PostMapping("/role")
    public R<Void> saveRole(@RequestBody SysRole role) {
        sysService.saveRole(role);
        return R.ok();
    }

    @PostMapping("/role/{id}/assign-menu")
    public R<Void> assignMenu(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysService.assignRoleMenus(id, menuIds);
        return R.ok();
    }

    // ===== 菜单管理 =====

    @GetMapping("/menu")
    public R<List<SysMenu>> menuTree() {
        return R.ok(sysService.menuTree());
    }

    @GetMapping("/menu/user")
    public R<List<SysMenu>> userMenus(@RequestParam Long userId) {
        return R.ok(sysService.getUserMenus(userId));
    }

    // ===== 操作日志 =====

    @GetMapping("/log/page")
    public R<IPage<SysOperationLog>> logPage(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(sysService.pageLog(module, page, pageSize));
    }
}
