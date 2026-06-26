package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Team;
import com.football.community.entity.TeamApplication;
import com.football.community.entity.TeamMember;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.TeamService;
import com.football.community.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberService teamMemberService;

    @GetMapping
    public Result<IPage<Team>> getTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(teamService.getTeamList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<Team> getTeam(@PathVariable Long id) {
        return Result.success(teamService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('team:add')")
    public Result<Team> createTeam(@RequestBody Map<String, Object> body,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Team team = new Team();
        team.setName((String) body.get("name"));
        team.setCity((String) body.get("city"));
        team.setCountry((String) body.get("country"));
        team.setStadium((String) body.get("stadium"));
        team.setDescription((String) body.get("description"));

        String memberType = (String) body.get("memberType");
        @SuppressWarnings("unchecked")
        Map<String, Object> memberInfo = (Map<String, Object>) body.get("memberInfo");

        return Result.success(teamService.createTeam(team, userDetails.getId(), memberType, memberInfo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Team> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        return Result.success(teamService.updateTeam(id, team));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    public Result<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return Result.success();
    }

    @GetMapping("/{teamId}/members")
    public Result<?> getMembers(@PathVariable Long teamId) {
        return Result.success(teamMemberService.getMembersByTeamId(teamId));
    }

    @GetMapping("/{teamId}/applications")
    @PreAuthorize("hasAuthority('team_member:approve')")
    public Result<List<TeamApplication>> getApplications(@PathVariable Long teamId) {
        return Result.success(teamMemberService.getApplicationsByTeamId(teamId));
    }

    @PostMapping("/{teamId}/members/apply")
    public Result<?> applyToJoin(@PathVariable Long teamId,
                                 @RequestBody Map<String, Object> body,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        String reason = (String) body.get("reason");
        String memberType = (String) body.get("memberType");
        @SuppressWarnings("unchecked")
        Map<String, Object> memberInfo = (Map<String, Object>) body.get("memberInfo");
        teamMemberService.applyToJoin(teamId, userDetails.getId(), reason, memberType, memberInfo);
        return Result.success();
    }

    @GetMapping("/my/invitations")
    public Result<List<Map<String, Object>>> getMyInvitations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(teamMemberService.getMyInvitations(userDetails.getId()));
    }

    @PutMapping("/invitations/{invitationId}/respond")
    public Result<?> respondToInvitation(@PathVariable Long invitationId,
                                         @RequestBody Map<String, Object> body,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Object acceptObj = body.get("accept");
        boolean accept = acceptObj instanceof Boolean ? (Boolean) acceptObj : Boolean.parseBoolean(String.valueOf(acceptObj));
        String memberType = (String) body.get("memberType");
        @SuppressWarnings("unchecked")
        Map<String, Object> memberInfo = (Map<String, Object>) body.get("memberInfo");
        teamMemberService.respondToInvitation(invitationId, accept, userDetails.getId(), memberType, memberInfo);
        return Result.success();
    }

    @PostMapping("/{teamId}/members/invite")
    @PreAuthorize("hasAuthority('team_member:invite')")
    public Result<?> inviteMember(@PathVariable Long teamId,
                                  @RequestParam Long userId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(teamMemberService.inviteMember(teamId, userId, userDetails.getId()));
    }

    @PutMapping("/{teamId}/members/{userId}/approve")
    @PreAuthorize("hasAuthority('team_member:approve')")
    public Result<?> approveApplication(@PathVariable Long teamId,
                                        @PathVariable Long userId,
                                        @RequestBody Map<String, Object> body,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer status = (Integer) body.get("status");
        String memberType = (String) body.get("memberType");
        @SuppressWarnings("unchecked")
        Map<String, Object> memberInfo = (Map<String, Object>) body.get("memberInfo");
        teamMemberService.approveApplication(teamId, userId, status, userDetails.getId(), memberType, memberInfo);
        return Result.success();
    }

    @PutMapping("/{teamId}/members/{userId}/remove")
    @PreAuthorize("hasAuthority('team_member:remove')")
    public Result<?> removeMember(@PathVariable Long teamId,
                                  @PathVariable Long userId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamMemberService.removeMember(teamId, userId, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('team_member:set_admin')")
    public Result<?> setMemberRole(@PathVariable Long teamId,
                                   @PathVariable Long userId,
                                   @RequestParam String role,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamMemberService.setMemberRole(teamId, userId, role, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/members/{userId}/status")
    @PreAuthorize("hasAuthority('team_member:set_status')")
    public Result<?> setMemberStatus(@PathVariable Long teamId,
                                     @PathVariable Long userId,
                                     @RequestBody Map<String, Object> body,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer status = (Integer) body.get("status");
        teamMemberService.setMemberStatus(teamId, userId, status, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/dissolve")
    @PreAuthorize("hasAuthority('team:dissolve')")
    public Result<?> dissolveTeam(@PathVariable Long teamId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamService.dissolveTeam(teamId, userDetails.getId());
        return Result.success();
    }
}
