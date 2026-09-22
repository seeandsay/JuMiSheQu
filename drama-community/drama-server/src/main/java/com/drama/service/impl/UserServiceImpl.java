package com.drama.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drama.common.BusinessException;
import com.drama.config.JwtUtils;
import com.drama.dto.LoginRequest;
import com.drama.dto.LoginResponse;
import com.drama.dto.RegisterRequest;
import com.drama.entity.User;
import com.drama.mapper.UserMapper;
import com.drama.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequest request) {
        // 手机号唯一校验
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该手机号已被注册");
        }
        // 昵称唯一校验
        wrapper.clear();
        wrapper.eq(User::getNickname, request.getNickname());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该昵称已被使用");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String account = request.getAccount();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, account)
               .or()
               .eq(User::getEmail, account);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        String token = jwtUtils.generateToken(user.getId());

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserInfo(LoginResponse.UserInfo.from(user));
        return resp;
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new BusinessException(401, "未登录");
    }

    @Override
    public void updateProfile(User user) {
        User currentUser = getCurrentUser();
        user.setId(currentUser.getId());
        // 不允许修改手机号和密码
        user.setPhone(null);
        user.setPassword(null);
        userMapper.updateById(user);
    }
}
