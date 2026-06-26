package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_registrations")
public class MatchRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long matchId;

    private Long teamId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String teamName;
}
