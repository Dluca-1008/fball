package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Player;

public interface PlayerService extends IService<Player> {

    IPage<Player> getPlayersByTeamId(Long teamId, int page, int size);

    IPage<Player> searchPlayers(String keyword, int page, int size);

    Player createPlayer(Player player);

    Player updatePlayer(Long id, Player player);

    void deletePlayer(Long id);
}
