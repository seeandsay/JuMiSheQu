package com.drama.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drama.dto.ReviewVO;

public interface ReviewService {

    Page<ReviewVO> getFeed(int page, int size);

    void createReview(Long userId, Long dramaId, Integer rating, String content);

    Page<ReviewVO> getMyReviews(Long userId, int page, int size);

    Page<ReviewVO> getDramaReviews(Long dramaId, int page, int size);
}
