package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Favorite;
import com.football.community.repository.FavoriteMapper;
import com.football.community.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    @Transactional
    public boolean toggleFavorite(Long userId, Long targetId, Integer targetType) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getTargetId, targetId)
               .eq(Favorite::getTargetType, targetType);
        Favorite existing = getOne(wrapper);

        if (existing != null) {
            removeById(existing.getId());
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setTargetId(targetId);
            favorite.setTargetType(targetType);
            favorite.setCreatedAt(LocalDateTime.now());
            save(favorite);
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long targetId, Integer targetType) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getTargetId, targetId)
               .eq(Favorite::getTargetType, targetType);
        return count(wrapper) > 0;
    }

    @Override
    public long countByTarget(Long targetId, Integer targetType) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getTargetId, targetId)
               .eq(Favorite::getTargetType, targetType);
        return count(wrapper);
    }
}
