package com.inkwell.auth.controller;

import com.inkwell.auth.common.ApiResponse;
import com.inkwell.auth.dto.LoginRequest;
import com.inkwell.auth.dto.RegisterRequest;
import com.inkwell.auth.entity.User;
import com.inkwell.auth.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ApiResponse.<User>builder()
                .success(true)
                .message("User registered successfully")
                .data(user)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Login successful")
                .data(token)
                .build();
    }
}