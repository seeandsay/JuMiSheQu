package com.drama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DramaCreateRequest {

    @NotBlank(message = "请输入剧集名称")
    @Size(max = 200, message = "剧集名称最多200个字符")
    private String name;

    @Size(max = 100, message = "类型最多100个字符")
    private String genre;

    private LocalDate releaseDate;

    private String description;

    @Size(max = 500, message = "海报链接最多500个字符")
    private String poster;
}
