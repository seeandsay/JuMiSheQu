package com.drama.controller;

import com.drama.common.Result;
import com.drama.dto.LoginRequest;
import com.drama.dto.LoginResponse;
import com.drama.dto.RegisterRequest;
import com.drama.entity.User;
import com.drama.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = userService.login(request);
        return Result.success(resp);
    }

    @GetMapping("/profile")
    public Result<LoginResponse.UserInfo> getProfile() {
        User user = userService.getCurrentUser();
        return Result.success(LoginResponse.UserInfo.from(user));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.success();
    }
}
