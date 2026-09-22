package com.drama.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drama.common.Result;
import com.drama.dto.ReviewVO;
import com.drama.entity.User;
import com.drama.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 首页信息流
     */
    @GetMapping("/review/feed")
    public Result<Page<ReviewVO>> getFeed(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getFeed(page, size));
    }

    /**
     * 发布评价
     */
    @PostMapping("/review")
    public Result<?> createReview(@RequestBody Map<String, Object> body) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long dramaId = Long.valueOf(body.get("dramaId").toString());
        Integer rating = (Integer) body.get("rating");
        String content = (String) body.get("content");
        reviewService.createReview(currentUser.getId(), dramaId, rating, content);
        return Result.success();
    }

    /**
     * 我的评价
     */
    @GetMapping("/review/my")
    public Result<Page<ReviewVO>> getMyReviews(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(reviewService.getMyReviews(currentUser.getId(), page, size));
    }

    /**
     * 指定剧集的评价列表
     */
    @GetMapping("/drama/{dramaId}/reviews")
    public Result<Page<ReviewVO>> getDramaReviews(@PathVariable Long dramaId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(reviewService.getDramaReviews(dramaId, page, size));
    }
}
