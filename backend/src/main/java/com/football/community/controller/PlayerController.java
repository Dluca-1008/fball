package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Player;
import com.football.community.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public Result<IPage<Player>> getPlayers(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (teamId != null) {
            return Result.success(playerService.getPlayersByTeamId(teamId, page, size));
        }
        return Result.success(playerService.searchPlayers(keyword, page, size));
    }

    @GetMapping("/{id}")
    public Result<Player> getPlayer(@PathVariable Long id) {
        return Result.success(playerService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Player> createPlayer(@RequestBody Player player) {
        return Result.success(playerService.createPlayer(player));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return Result.success(playerService.updatePlayer(id, player));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    public Result<?> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return Result.success();
    }
}
