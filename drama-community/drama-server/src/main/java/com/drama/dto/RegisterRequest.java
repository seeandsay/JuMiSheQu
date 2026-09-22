package com.drama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "请输入手机号")
    private String phone;

    @NotBlank(message = "请输入昵称")
    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    @NotBlank(message = "请设置密码")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
}
