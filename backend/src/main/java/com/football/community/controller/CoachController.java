package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Coach;
import com.football.community.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @GetMapping
    public Result<IPage<Coach>> getCoaches(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (teamId != null) {
            return Result.success(coachService.getCoachesByTeamId(teamId, page, size));
        }
        return Result.success(coachService.searchCoaches(keyword, page, size));
    }

    @GetMapping("/{id}")
    public Result<Coach> getCoach(@PathVariable Long id) {
        return Result.success(coachService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Coach> createCoach(@RequestBody Coach coach) {
        return Result.success(coachService.createCoach(coach));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Coach> updateCoach(@PathVariable Long id, @RequestBody Coach coach) {
        return Result.success(coachService.updateCoach(id, coach));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    public Result<?> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return Result.success();
    }
}
