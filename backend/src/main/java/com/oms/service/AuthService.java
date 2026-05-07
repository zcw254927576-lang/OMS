package com.oms.service;

import com.oms.dto.LoginDTO;
import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    /** 用户登录 */
    Map<String, Object> login(LoginDTO dto);

    /** 获取当前用户信息及菜单权限 */
    Map<String, Object> getUserInfo(Long userId);
}
