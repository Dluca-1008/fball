package com.football.community.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InviteFriendRequest {

    @NotNull(message = "邀请目标球队ID不能为空")
    private Long receiverTeamId;

    @NotNull(message = "比赛时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchDate;

    @NotBlank(message = "比赛场地不能为空")
    private String venue;
}
