package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.RegisterDto;
import com.football.community.entity.Match;
import com.football.community.entity.Team;
import com.football.community.entity.User;
import com.football.community.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    private User testUser;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("teamadmin");
        registerDto.setPassword("test123456");
        registerDto.setNickname("球队管理员");
        testUser = userService.register(registerDto);

        testTeam = new Team();
        testTeam.setName("测试球队");
        testTeam.setCity("北京");
        testTeam.setCountry("中国");
    }

    @Test
    void testCreateTeam() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        assertNotNull(team);
        assertNotNull(team.getId());
        assertEquals("测试球队", team.getName());
        assertEquals(testUser.getId(), team.getCreatedBy());
    }

    @Test
    void testGetTeamList() {
        teamService.createTeam(testTeam, testUser.getId(), "player", null);

        IPage<Team> teams = teamService.getTeamList(1, 10, null);

        assertNotNull(teams);
        assertTrue(teams.getRecords().size() > 0);
    }

    @Test
    void testGetTeamListWithKeyword() {
        teamService.createTeam(testTeam, testUser.getId(), "player", null);

        IPage<Team> teams = teamService.getTeamList(1, 10, "测试");

        assertNotNull(teams);
        assertTrue(teams.getRecords().size() > 0);
    }

    @Test
    void testUpdateTeam() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        Team updateTeam = new Team();
        updateTeam.setName("更新后的球队");
        Team updated = teamService.updateTeam(team.getId(), updateTeam);

        assertEquals("更新后的球队", updated.getName());
    }

    @Test
    void testDeleteTeam() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        teamService.deleteTeam(team.getId());

        Team deleted = teamService.getById(team.getId());
        assertNull(deleted);
    }

    @Test
    void testIsTeamAdmin() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        boolean isAdmin = teamService.isTeamAdmin(team.getId(), testUser.getId());

        assertTrue(isAdmin);
    }

    @Test
    void testIsTeamMember() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        boolean isMember = teamService.isTeamMember(team.getId(), testUser.getId());

        assertTrue(isMember);
    }

    @Test
    void testIsNotTeamMember() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("otheruser");
        registerDto.setPassword("test123456");
        User otherUser = userService.register(registerDto);

        boolean isMember = teamService.isTeamMember(team.getId(), otherUser.getId());

        assertFalse(isMember);
    }

    @Test
    void testDissolveTeam() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        teamService.dissolveTeam(team.getId(), testUser.getId());

        Team dissolved = teamService.getById(team.getId());
        assertNotNull(dissolved);
        assertEquals(0, dissolved.getStatus());
    }

    @Test
    void testDissolveTeamNotAdmin() {
        Team team = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("nonadmin");
        registerDto.setPassword("test123456");
        User otherUser = userService.register(registerDto);

        assertThrows(BusinessException.class, () -> {
            teamService.dissolveTeam(team.getId(), otherUser.getId());
        });
    }

    @Test
    void testDissolveTeamWithUnfinishedMatch() {
        Team team1 = teamService.createTeam(testTeam, testUser.getId(), "player", null);

        Team team2 = new Team();
        team2.setName("对手球队");
        team2.setCity("上海");
        team2.setCountry("中国");
        Team createdTeam2 = teamService.createTeam(team2, testUser.getId(), "player", null);

        Match match = new Match();
        match.setHomeTeamId(team1.getId());
        match.setAwayTeamId(createdTeam2.getId());
        match.setStatus(0);
        match.setMatchDate(java.time.LocalDateTime.now().plusDays(1));
        matchService.createMatch(match);

        assertThrows(BusinessException.class, () -> {
            teamService.dissolveTeam(team1.getId(), testUser.getId());
        });
    }
}
