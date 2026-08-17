package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_friend_requests")
public class MatchFriendRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long senderTeamId;

    private Long receiverTeamId;

    private LocalDateTime matchDate;

    private String venue;

    private Integer status;

    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
