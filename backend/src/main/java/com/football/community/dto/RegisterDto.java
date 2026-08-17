package com.football.community.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 10, message = "用户名长度为2-10个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    private String email;

    private String phone;

    private String nickname;
}
