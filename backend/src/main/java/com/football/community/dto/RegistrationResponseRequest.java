package com.football.community.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegistrationResponseRequest {

    @NotNull(message = "响应结果不能为空")
    private Boolean accept;
}
