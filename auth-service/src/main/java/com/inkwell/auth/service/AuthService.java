package com.inkwell.auth.service;

import com.inkwell.auth.dto.LoginRequest;
import com.inkwell.auth.dto.RegisterRequest;
import com.inkwell.auth.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    String login(LoginRequest request);
}