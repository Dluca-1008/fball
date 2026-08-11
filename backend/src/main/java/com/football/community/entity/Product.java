package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Long categoryId;

    private Integer stock;

    private Integer salesCount;

    @Version
    private Integer version;

    private String images;

    private Integer status;

    private Long merchantId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
