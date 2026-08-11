package com.football.community.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleGenerateRequest {

    @NotNull(message = "队伍列表不能为空")
    private List<Long> teamIds;

    @NotNull(message = "开始日期不能为空")
    private LocalDateTime startDate;

    private Integer intervalDays = 7;

    private String matchType = "league";

    private Map<String, List<Long>> groupTeams;
}
