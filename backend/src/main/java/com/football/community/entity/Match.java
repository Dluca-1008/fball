package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("matches")
public class Match {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long homeTeamId;

    private Long awayTeamId;

    private Long leagueId;

    private String matchType;

    private LocalDateTime matchDate;

    private String venue;

    private Integer status;

    private Integer homeScore;

    private Integer awayScore;

    private Integer homeScoreHalf;

    private Integer awayScoreHalf;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String homeTeamName;

    @TableField(exist = false)
    private String awayTeamName;
}
