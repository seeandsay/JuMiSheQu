package com.drama.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drama.common.BusinessException;
import com.drama.dto.DramaCreateRequest;
import com.drama.entity.Drama;
import com.drama.mapper.DramaMapper;
import com.drama.service.DramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DramaServiceImpl implements DramaService {

    private final DramaMapper dramaMapper;

    @Override
    public List<Drama> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Drama> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Drama::getName, keyword)
               .orderByDesc(Drama::getCreateTime);
        return dramaMapper.selectList(wrapper);
    }

    @Override
    public Drama getById(Long id) {
        Drama drama = dramaMapper.selectById(id);
        if (drama == null) {
            throw new BusinessException("剧集不存在");
        }
        return drama;
    }

    @Override
    public Drama createDrama(DramaCreateRequest request) {
        // 剧名精确去重
        LambdaQueryWrapper<Drama> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drama::getName, request.getName());
        if (dramaMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该剧集已存在");
        }

        Drama drama = new Drama();
        drama.setName(request.getName());
        drama.setGenre(request.getGenre());
        drama.setReleaseDate(request.getReleaseDate());
        drama.setDescription(request.getDescription());
        drama.setPoster(request.getPoster());
        dramaMapper.insert(drama);
        return drama;
    }
}
