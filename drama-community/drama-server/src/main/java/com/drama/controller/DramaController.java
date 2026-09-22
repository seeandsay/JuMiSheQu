package com.drama.controller;

import com.drama.common.Result;
import com.drama.dto.DramaCreateRequest;
import com.drama.entity.Drama;
import com.drama.service.DramaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drama")
@RequiredArgsConstructor
public class DramaController {

    private final DramaService dramaService;

    /**
     * 搜索剧集
     */
    @GetMapping("/search")
    public Result<List<Drama>> search(@RequestParam String keyword) {
        return Result.success(dramaService.search(keyword));
    }

    /**
     * 剧集详情
     */
    @GetMapping("/{id}")
    public Result<Drama> getDetail(@PathVariable Long id) {
        return Result.success(dramaService.getById(id));
    }

    /**
     * 创建剧集
     */
    @PostMapping
    public Result<Drama> create(@Valid @RequestBody DramaCreateRequest request) {
        return Result.success(dramaService.createDrama(request));
    }
}
