package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Like;

/**
 * 点赞服务接口。
 * <p>提供点赞/取消点赞、状态检查和计数等业务逻辑。</p>
 */
public interface LikeService extends IService<Like> {

    /**
     * 切换点赞状态。
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return true-已点赞, false-已取消
     */
    boolean toggleLike(Long userId, Long targetId, Integer targetType);

    /**
     * 检查用户是否已点赞指定目标。
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return true-已点赞, false-未点赞
     */
    boolean isLiked(Long userId, Long targetId, Integer targetType);

    /**
     * 获取指定目标的点赞数。
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 点赞数量
     */
    long countByTarget(Long targetId, Integer targetType);
}
