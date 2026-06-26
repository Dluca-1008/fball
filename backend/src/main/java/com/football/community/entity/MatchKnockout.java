package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_knockouts")
public class MatchKnockout {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long matchId;

    private String round;

    private Integer position;

    private Long homeTeamId;

    private Long awayTeamId;

    private Integer homeScore;

    private Integer awayScore;

    private Long winnerTeamId;

    private Long nextMatchId;

    private LocalDateTime matchDate;

    private String venue;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String homeTeamName;

    @TableField(exist = false)
    private String awayTeamName;

    @TableField(exist = false)
    private String winnerTeamName;
}
