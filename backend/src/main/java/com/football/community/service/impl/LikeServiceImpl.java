package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Like;
import com.football.community.repository.LikeMapper;
import com.football.community.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long targetId, Integer targetType) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
               .eq(Like::getTargetId, targetId)
               .eq(Like::getTargetType, targetType);
        Like existing = getOne(wrapper);

        if (existing != null) {
            removeById(existing.getId());
            return false;
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            like.setCreatedAt(LocalDateTime.now());
            save(like);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, Long targetId, Integer targetType) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
               .eq(Like::getTargetId, targetId)
               .eq(Like::getTargetType, targetType);
        return count(wrapper) > 0;
    }

    @Override
    public long countByTarget(Long targetId, Integer targetType) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getTargetId, targetId)
               .eq(Like::getTargetType, targetType);
        return count(wrapper);
    }
}
