package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Player;

/**
 * 球员服务接口。
 * <p>提供球员CRUD及按队伍、关键词查询等业务逻辑。</p>
 */
public interface PlayerService extends IService<Player> {

    /**
     * 按队伍ID分页获取球员列表。
     * @param teamId 队伍ID
     * @param page 页码
     * @param size 每页大小
     * @return 球员分页结果
     */
    IPage<Player> getPlayersByTeamId(Long teamId, int page, int size);

    /**
     * 按关键词搜索球员。
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 球员分页结果
     */
    IPage<Player> searchPlayers(String keyword, int page, int size);

    /**
     * 创建新球员。
     * @param player 球员实体
     * @return 创建后的球员
     */
    Player createPlayer(Player player);

    /**
     * 更新球员信息。
     * @param id 球员ID
     * @param player 球员实体
     * @return 更新后的球员
     */
    Player updatePlayer(Long id, Player player);

    /**
     * 删除球员。
     * @param id 球员ID
     */
    void deletePlayer(Long id);
}
