package com.drama.service;

import com.drama.dto.LoginRequest;
import com.drama.dto.LoginResponse;
import com.drama.dto.RegisterRequest;
import com.drama.entity.User;

public interface UserService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User getCurrentUser();

    void updateProfile(User user);
}
