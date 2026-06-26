package com.football.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime paymentTime;

    private LocalDateTime deliveryTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String productImage;
}
