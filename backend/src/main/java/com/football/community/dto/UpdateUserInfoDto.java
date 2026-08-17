package com.football.community.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDto {

    private String nickname;

    private String email;

    private String phone;

    private Integer gender;

    private String avatar;
}
