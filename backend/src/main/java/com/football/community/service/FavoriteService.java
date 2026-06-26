package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {

    boolean toggleFavorite(Long userId, Long targetId, Integer targetType);

    boolean isFavorited(Long userId, Long targetId, Integer targetType);

    long countByTarget(Long targetId, Integer targetType);
}
