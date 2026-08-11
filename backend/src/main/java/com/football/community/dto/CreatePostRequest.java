package com.football.community.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String category;
}
