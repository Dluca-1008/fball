package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.football.community.entity.*;
import com.football.community.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PostService postService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Override
    public Map<String, Object> getAdminOverview() {
        Map<String, Object> result = new HashMap<>();

        result.put("totalUsers", userService.count());
        result.put("totalTeams", teamService.count());
        result.put("totalMatches", matchService.count());
        result.put("totalPosts", postService.count());
        result.put("totalProducts", productService.count());

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.ge(Order::getCreatedAt, LocalDateTime.now().with(LocalTime.MIN));
        result.put("todayOrders", orderService.count(orderWrapper));

        return result;
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();

        long todayNew = userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreatedAt, LocalDateTime.now().with(LocalTime.MIN)));
        result.put("todayNewUsers", todayNew);

        long weekNew = userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreatedAt, LocalDateTime.now().minusDays(7)));
        result.put("weekNewUsers", weekNew);

        long monthNew = userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreatedAt, LocalDateTime.now().minusDays(30)));
        result.put("monthNewUsers", monthNew);

        return result;
    }

    @Override
    public Map<String, Object> getMatchStatistics() {
        Map<String, Object> result = new HashMap<>();

        result.put("totalMatches", matchService.count());

        LambdaQueryWrapper<Match> upcomingWrapper = new LambdaQueryWrapper<>();
        upcomingWrapper.gt(Match::getMatchDate, LocalDateTime.now())
                       .eq(Match::getStatus, 0);
        result.put("upcomingMatches", matchService.count(upcomingWrapper));

        LambdaQueryWrapper<Match> ongoingWrapper = new LambdaQueryWrapper<>();
        ongoingWrapper.eq(Match::getStatus, 1);
        result.put("ongoingMatches", matchService.count(ongoingWrapper));

        return result;
    }

    @Override
    public Map<String, Object> getSalesStatistics(Long merchantId) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        if (merchantId != null) {
            productWrapper.eq(Product::getMerchantId, merchantId);
        }
        result.put("totalProducts", productService.count(productWrapper));

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getStatus, 1);
        if (merchantId != null) {
            orderWrapper.inSql(Order::getProductId,
                    "SELECT id FROM products WHERE merchant_id = " + merchantId);
        }
        result.put("totalOrders", orderService.count(orderWrapper));

        return result;
    }

    @Override
    public Map<String, Object> getTeamStatistics(Long teamId) {
        Map<String, Object> result = new HashMap<>();

        result.put("teamName", teamService.getById(teamId).getName());

        LambdaQueryWrapper<Match> matchWrapper = new LambdaQueryWrapper<>();
        matchWrapper.and(w -> w.eq(Match::getHomeTeamId, teamId)
                              .or().eq(Match::getAwayTeamId, teamId));
        result.put("totalMatches", matchService.count(matchWrapper));

        LambdaQueryWrapper<Match> winWrapper = new LambdaQueryWrapper<>();
        winWrapper.and(w -> w.eq(Match::getHomeTeamId, teamId)
                             .apply("home_score > away_score")
                             .or()
                             .eq(Match::getAwayTeamId, teamId)
                             .apply("away_score > home_score"));
        result.put("wins", matchService.count(winWrapper));

        return result;
    }
}
