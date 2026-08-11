package com.football.community.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TeamRegisterRequest {

    @NotNull(message = "队伍ID不能为空")
    private Long teamId;
}
