package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Team;
import com.football.community.entity.TeamApplication;
import com.football.community.entity.TeamMember;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.TeamService;
import com.football.community.service.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "球队管理", description = "球队及成员管理接口")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberService teamMemberService;

    @GetMapping
    @Operation(summary = "获取球队列表", description = "分页查询球队，支持关键词搜索")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<IPage<Team>> getTeams(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(teamService.getTeamList(page, size, keyword));
    }

    @GetMapping("/list")
    @Operation(summary = "获取球队列表(全部)", description = "返回所有球队，不分页")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<List<Team>> getTeamList() {
        return Result.success(teamService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取球队详情", description = "根据ID获取球队详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Team> getTeam(@Parameter(description = "球队ID", example = "1") @PathVariable Long id) {
        return Result.success(teamService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('team:add')")
    @Operation(summary = "创建球队", description = "创建新球队并设置管理员")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Team> createTeam(@Parameter(description = "球队信息") @RequestBody Map<String, Object> body,
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
    @Operation(summary = "更新球队", description = "根据ID更新球队信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Team> updateTeam(@Parameter(description = "球队ID", example = "1") @PathVariable Long id, @Parameter(description = "球队信息") @RequestBody Team team) {
        return Result.success(teamService.updateTeam(id, team));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    @Operation(summary = "删除球队", description = "根据ID删除球队")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> deleteTeam(@Parameter(description = "球队ID", example = "1") @PathVariable Long id) {
        teamService.deleteTeam(id);
        return Result.success();
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "获取球队成员", description = "获取指定球队的所有成员列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> getMembers(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId) {
        return Result.success(teamMemberService.getMembersByTeamId(teamId));
    }

    @GetMapping("/{teamId}/applications")
    @PreAuthorize("hasAuthority('team_member:approve')")
    @Operation(summary = "获取入队申请列表", description = "获取指定球队的入队申请列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<TeamApplication>> getApplications(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId) {
        return Result.success(teamMemberService.getApplicationsByTeamId(teamId));
    }

    @PostMapping("/{teamId}/members/apply")
    @Operation(summary = "申请加入球队", description = "用户申请加入指定球队")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> applyToJoin(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                 @Parameter(description = "申请信息") @RequestBody Map<String, Object> body,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        String reason = (String) body.get("reason");
        String memberType = (String) body.get("memberType");
        @SuppressWarnings("unchecked")
        Map<String, Object> memberInfo = (Map<String, Object>) body.get("memberInfo");
        teamMemberService.applyToJoin(teamId, userDetails.getId(), reason, memberType, memberInfo);
        return Result.success();
    }

    @GetMapping("/my/invitations")
    @Operation(summary = "获取我的邀请", description = "获取当前用户收到的加入球队邀请")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<Map<String, Object>>> getMyInvitations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(teamMemberService.getMyInvitations(userDetails.getId()));
    }

    @PutMapping("/invitations/{invitationId}/respond")
    @Operation(summary = "响应邀请", description = "接受或拒绝加入球队的邀请")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> respondToInvitation(@Parameter(description = "邀请ID", example = "1") @PathVariable Long invitationId,
                                         @Parameter(description = "响应信息") @RequestBody Map<String, Object> body,
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
    @Operation(summary = "邀请用户加入", description = "邀请指定用户加入球队")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> inviteMember(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                  @Parameter(description = "用户ID") @RequestParam Long userId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(teamMemberService.inviteMember(teamId, userId, userDetails.getId()));
    }

    @PutMapping("/{teamId}/members/{userId}/approve")
    @PreAuthorize("hasAuthority('team_member:approve')")
    @Operation(summary = "审批入队申请", description = "审批用户的入队申请，通过或拒绝")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> approveApplication(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                        @Parameter(description = "用户ID", example = "1") @PathVariable Long userId,
                                        @Parameter(description = "审批信息") @RequestBody Map<String, Object> body,
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
    @Operation(summary = "移除成员", description = "将指定用户从球队中移除")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> removeMember(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                  @Parameter(description = "用户ID", example = "1") @PathVariable Long userId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamMemberService.removeMember(teamId, userId, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('team_member:set_admin')")
    @Operation(summary = "设置成员角色", description = "修改指定用户在球队中的角色")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> setMemberRole(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                   @Parameter(description = "用户ID", example = "1") @PathVariable Long userId,
                                   @Parameter(description = "角色名称") @RequestParam String role,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamMemberService.setMemberRole(teamId, userId, role, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/members/{userId}/status")
    @PreAuthorize("hasAuthority('team_member:set_status')")
    @Operation(summary = "设置成员状态", description = "修改指定用户在球队中的状态（正常/禁言/封禁）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> setMemberStatus(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                     @Parameter(description = "用户ID", example = "1") @PathVariable Long userId,
                                     @Parameter(description = "状态信息") @RequestBody Map<String, Object> body,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer status = (Integer) body.get("status");
        teamMemberService.setMemberStatus(teamId, userId, status, userDetails.getId());
        return Result.success();
    }

    @PutMapping("/{teamId}/dissolve")
    @PreAuthorize("hasAuthority('team:dissolve')")
    @Operation(summary = "解散球队", description = "解散指定球队")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> dissolveTeam(@Parameter(description = "球队ID", example = "1") @PathVariable Long teamId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        teamService.dissolveTeam(teamId, userDetails.getId());
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的球队", description = "获取当前用户所属的球队")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<?> getMyTeam(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teamId = teamService.getUserTeamId(userDetails.getId());
        if (teamId == null) {
            return Result.success(null);
        }
        Team team = teamService.getById(teamId);
        return Result.success(team);
    }
}
