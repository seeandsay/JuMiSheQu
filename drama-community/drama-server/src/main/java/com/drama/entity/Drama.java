package com.drama.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("drama")
public class Drama {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String poster;

    private String description;

    private String genre;

    private LocalDate releaseDate;

    private LocalDateTime createTime;
}
