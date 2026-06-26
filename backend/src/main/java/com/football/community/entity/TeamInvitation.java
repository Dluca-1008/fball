package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_invitations")
public class TeamInvitation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long inviterId;

    private Long inviteeId;

    private String inviteCode;

    private Integer status;

    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
