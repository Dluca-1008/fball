package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Like;

public interface LikeService extends IService<Like> {

    boolean toggleLike(Long userId, Long targetId, Integer targetType);

    boolean isLiked(Long userId, Long targetId, Integer targetType);

    long countByTarget(Long targetId, Integer targetType);
}
