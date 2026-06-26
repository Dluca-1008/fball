package com.football.community.service;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getAdminOverview();

    Map<String, Object> getUserStatistics();

    Map<String, Object> getMatchStatistics();

    Map<String, Object> getSalesStatistics(Long merchantId);

    Map<String, Object> getTeamStatistics(Long teamId);
}
