package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_cards")
public class MatchCard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long matchId;

    private Long teamId;

    private Long playerId;

    private Integer cardType;

    private Integer minute;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String teamName;

    @TableField(exist = false)
    private String playerName;
}
