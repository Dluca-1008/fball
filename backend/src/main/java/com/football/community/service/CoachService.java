package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Coach;

/**
 * 教练服务接口。
 * <p>提供教练CRUD及按队伍、关键词查询等业务逻辑。</p>
 */
public interface CoachService extends IService<Coach> {

    /**
     * 按队伍ID分页获取教练列表。
     * @param teamId 队伍ID
     * @param page 页码
     * @param size 每页大小
     * @return 教练分页结果
     */
    IPage<Coach> getCoachesByTeamId(Long teamId, int page, int size);

    /**
     * 按关键词搜索教练。
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 教练分页结果
     */
    IPage<Coach> searchCoaches(String keyword, int page, int size);

    /**
     * 创建新教练。
     * @param coach 教练实体
     * @return 创建后的教练
     */
    Coach createCoach(Coach coach);

    /**
     * 更新教练信息。
     * @param id 教练ID
     * @param coach 教练实体
     * @return 更新后的教练
     */
    Coach updateCoach(Long id, Coach coach);

    /**
     * 删除教练。
     * @param id 教练ID
     */
    void deleteCoach(Long id);

    /**
     * 检查用户是否已注册为教练。
     * @param userId 用户ID
     * @return true-已注册, false-未注册
     */
    boolean isCoachRegistered(Long userId);
}
