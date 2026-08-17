package com.football.community.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateMatchRequest {

    @NotBlank(message = "赛事名称不能为空")
    private String name;

    private String matchType;

    @NotNull(message = "比赛时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchDate;

    private String venue;
}
