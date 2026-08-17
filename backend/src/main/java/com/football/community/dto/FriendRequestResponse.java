package com.football.community.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class FriendRequestResponse {

    @NotNull(message = "是否接受不能为空")
    private Boolean accept;
}
