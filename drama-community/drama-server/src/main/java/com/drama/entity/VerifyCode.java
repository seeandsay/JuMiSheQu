package com.drama.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verify_code")
public class VerifyCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String target;

    private String code;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;
}
