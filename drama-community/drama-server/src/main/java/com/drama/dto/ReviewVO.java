package com.drama.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewVO {

    private Long id;
    private Long userId;
    private Long dramaId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
    private String userNickname;
    private String dramaName;
}
