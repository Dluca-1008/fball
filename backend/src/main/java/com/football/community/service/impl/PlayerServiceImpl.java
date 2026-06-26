package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Player;
import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.PlayerMapper;
import com.football.community.service.PlayerService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlayerServiceImpl extends ServiceImpl<PlayerMapper, Player> implements PlayerService {

    @Autowired
    private TeamService teamService;

    @Override
    public IPage<Player> getPlayersByTeamId(Long teamId, int page, int size) {
        LambdaQueryWrapper<Player> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Player::getTeamId, teamId)
               .orderByAsc(Player::getNumber);
        IPage<Player> playerPage = page(new Page<>(page, size), wrapper);

        playerPage.getRecords().forEach(this::fillTeamName);

        return playerPage;
    }

    @Override
    public IPage<Player> searchPlayers(String keyword, int page, int size) {
        LambdaQueryWrapper<Player> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Player::getName, keyword)
                   .or().like(Player::getNationality, keyword);
        }
        wrapper.orderByDesc(Player::getCreatedAt);
        IPage<Player> playerPage = page(new Page<>(page, size), wrapper);

        playerPage.getRecords().forEach(this::fillTeamName);

        return playerPage;
    }

    @Override
    public Player createPlayer(Player player) {
        player.setCreatedAt(LocalDateTime.now());
        save(player);
        fillTeamName(player);
        return player;
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        Player existing = getById(id);
        if (existing == null) {
            throw new BusinessException("球员不存在");
        }
        player.setId(id);
        updateById(player);
        return getById(id);
    }

    @Override
    public void deletePlayer(Long id) {
        removeById(id);
    }

    private void fillTeamName(Player player) {
        if (player.getTeamId() != null) {
            Team team = teamService.getById(player.getTeamId());
            if (team != null) {
                if (team.getStatus() != null && team.getStatus() == 0) {
                    player.setTeamName(team.getName() + "（已解散）");
                } else {
                    player.setTeamName(team.getName());
                }
            }
        }
    }
}
