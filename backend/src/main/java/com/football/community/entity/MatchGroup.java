package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_groups")
public class MatchGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long matchId;

    private String groupName;

    private Long teamId;

    private Integer played;

    private Integer won;

    private Integer drawn;

    private Integer lost;

    private Integer goalsFor;

    private Integer goalsAgainst;

    private Integer points;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String teamName;

    @TableField(exist = false)
    private Integer goalDifference;
}
