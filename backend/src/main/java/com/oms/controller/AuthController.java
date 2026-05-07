package com.oms.controller;

import com.oms.common.R;
import com.oms.dto.LoginDTO;
import com.oms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(authService.login(dto));
    }

    @GetMapping("/user-info")
    public R<Map<String, Object>> getUserInfo(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return R.ok(authService.getUserInfo(userId));
    }
}
