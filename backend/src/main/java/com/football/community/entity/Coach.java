package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coaches")
public class Coach {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long teamId;

    private String name;

    private String roleTitle;

    private String nationality;

    private LocalDate birthDate;

    private Integer experienceYears;

    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String teamName;
}
