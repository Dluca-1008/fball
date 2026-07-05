package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Favorite;

/**
 * 收藏服务接口。
 * <p>提供收藏/取消收藏、状态检查和计数等业务逻辑。</p>
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 切换收藏状态。
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return true-已收藏, false-已取消
     */
    boolean toggleFavorite(Long userId, Long targetId, Integer targetType);

    /**
     * 检查用户是否已收藏指定目标。
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return true-已收藏, false-未收藏
     */
    boolean isFavorited(Long userId, Long targetId, Integer targetType);

    /**
     * 获取指定目标的收藏数。
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    long countByTarget(Long targetId, Integer targetType);
}
