package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Team;

import java.util.List;
import java.util.Map;

/**
 * 队伍服务接口。
 * <p>提供队伍CRUD、创建、解散及成员资格校验等业务逻辑。</p>
 */
public interface TeamService extends IService<Team> {

    /**
     * 分页获取队伍列表。
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 队伍分页结果
     */
    IPage<Team> getTeamList(int page, int size, String keyword);

    /**
     * 创建新队伍。
     * @param team 队伍实体
     * @param userId 创建人ID
     * @param memberType 成员类型
     * @param memberInfo 成员信息
     * @return 创建后的队伍
     */
    Team createTeam(Team team, Long userId, String memberType, Map<String, Object> memberInfo);

    /**
     * 更新队伍信息。
     * @param id 队伍ID
     * @param team 队伍实体
     * @return 更新后的队伍
     */
    Team updateTeam(Long id, Team team);

    /**
     * 删除队伍。
     * @param id 队伍ID
     */
    void deleteTeam(Long id);

    /**
     * 检查用户是否为队伍管理员。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @return true-是管理员, false-不是
     */
    boolean isTeamAdmin(Long teamId, Long userId);

    /**
     * 检查用户是否为队伍成员。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @return true-是成员, false-不是
     */
    boolean isTeamMember(Long teamId, Long userId);

    /**
     * 解散队伍。
     * @param teamId 队伍ID
     * @param userId 操作人ID
     */
    void dissolveTeam(Long teamId, Long userId);

    /**
     * 获取用户所属的球队ID列表。
     * @param userId 用户ID
     * @return 球队ID列表
     */
    List<Long> getUserTeamIds(Long userId);

    /**
     * 获取用户所属球队ID（优先返回管理员球队，无则返回任意一个）。
     * @param userId 用户ID
     * @return 球队ID，不存在则返回null
     */
    Long getUserTeamId(Long userId);
}
