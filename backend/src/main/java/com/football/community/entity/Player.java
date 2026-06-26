package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("players")
public class Player {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private String name;

    private String position;

    private Integer number;

    private String nationality;

    private LocalDate birthDate;

    private BigDecimal height;

    private BigDecimal weight;

    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String teamName;
}
