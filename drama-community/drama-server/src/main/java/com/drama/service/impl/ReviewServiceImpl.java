package com.drama.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drama.common.BusinessException;
import com.drama.dto.ReviewVO;
import com.drama.entity.Drama;
import com.drama.entity.Review;
import com.drama.entity.User;
import com.drama.mapper.DramaMapper;
import com.drama.mapper.ReviewMapper;
import com.drama.mapper.UserMapper;
import com.drama.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final DramaMapper dramaMapper;

    @Override
    public Page<ReviewVO> getFeed(int page, int size) {
        Page<Review> reviewPage = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Review::getCreateTime);
        reviewMapper.selectPage(reviewPage, wrapper);

        return convertToVO(reviewPage);
    }

    @Override
    public void createReview(Long userId, Long dramaId, Integer rating, String content) {
        if (rating < 1 || rating > 10) {
            throw new BusinessException("评分范围为1-10");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("评价内容不能为空");
        }
        if (content.length() > 500) {
            throw new BusinessException("评价内容最多500字");
        }
        // 校验剧集是否存在
        Drama drama = dramaMapper.selectById(dramaId);
        if (drama == null) {
            throw new BusinessException("剧集不存在");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setDramaId(dramaId);
        review.setRating(rating);
        review.setContent(content.trim());
        reviewMapper.insert(review);
    }

    @Override
    public Page<ReviewVO> getMyReviews(Long userId, int page, int size) {
        Page<Review> reviewPage = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId)
               .orderByDesc(Review::getCreateTime);
        reviewMapper.selectPage(reviewPage, wrapper);

        return convertToVO(reviewPage);
    }

    @Override
    public Page<ReviewVO> getDramaReviews(Long dramaId, int page, int size) {
        Page<Review> reviewPage = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getDramaId, dramaId)
               .orderByDesc(Review::getCreateTime);
        reviewMapper.selectPage(reviewPage, wrapper);

        return convertToVO(reviewPage);
    }

    /**
     * 批量查询用户和剧集信息，组装 VO
     */
    private Page<ReviewVO> convertToVO(Page<Review> reviewPage) {
        List<Review> reviews = reviewPage.getRecords();

        // 收集所有 userId 和 dramaId
        List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().collect(Collectors.toList());
        List<Long> dramaIds = reviews.stream().map(Review::getDramaId).distinct().collect(Collectors.toList());

        // 批量查询
        Map<Long, String> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));
        Map<Long, String> dramaMap = dramaMapper.selectBatchIds(dramaIds).stream()
                .collect(Collectors.toMap(Drama::getId, Drama::getName));

        // 组装 VO
        List<ReviewVO> voList = reviews.stream().map(r -> {
            ReviewVO vo = new ReviewVO();
            vo.setId(r.getId());
            vo.setUserId(r.getUserId());
            vo.setDramaId(r.getDramaId());
            vo.setRating(r.getRating());
            vo.setContent(r.getContent());
            vo.setCreateTime(r.getCreateTime());
            vo.setUserNickname(userMap.getOrDefault(r.getUserId(), "未知用户"));
            vo.setDramaName(dramaMap.getOrDefault(r.getDramaId(), "未知剧集"));
            return vo;
        }).collect(Collectors.toList());

        Page<ReviewVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(reviewPage.getTotal());
        voPage.setCurrent(reviewPage.getCurrent());
        voPage.setSize(reviewPage.getSize());
        return voPage;
    }
}
