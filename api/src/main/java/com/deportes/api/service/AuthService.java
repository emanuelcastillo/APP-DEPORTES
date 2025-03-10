package com.deportes.api.service;

import com.deportes.api.records.LoginRequest;
import com.deportes.api.records.RegisterRequest;

public interface AuthService {
    String login(LoginRequest request);

    void register(RegisterRequest request);
}
