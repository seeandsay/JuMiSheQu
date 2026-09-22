package com.drama.service;

import com.drama.dto.DramaCreateRequest;
import com.drama.entity.Drama;

import java.util.List;

public interface DramaService {

    List<Drama> search(String keyword);

    Drama getById(Long id);

    Drama createDrama(DramaCreateRequest request);
}
